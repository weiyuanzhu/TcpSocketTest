package com.mackwell.nlight.nlight;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.mackwell.nlight.R;
import com.mackwell.nlight.models.Panel;
import com.mackwell.nlight.nlight.InputDialogFragment.NoticeDialogListener;
import com.mackwell.nlight.nlight.PanelListFragment.OnPanelListItemClickedCallBack;
import com.mackwell.nlight.socket.TCPConnection;
import com.mackwell.nlight.util.CommandFactory;
import com.mackwell.nlight.util.DataParser;
import com.mackwell.nlight.util.SetCmdEnum;

/**
 * @author weiyuan zhu
 *
 */
public class PanelActivity extends BaseActivity implements OnPanelListItemClickedCallBack, TCPConnection.CallBack, PopupMenu.OnMenuItemClickListener, NoticeDialogListener{
	
	private List<Panel> panelList = null;
	private Map<String,Panel> panelMap = null;
	private Map<String,TCPConnection> ip_connection_map = null;
	private Map<String,List<Integer>> rxBufferMap = null;
	private List<char[] > commandList = null;
	
	private List<PanelInfoFragment> fragmentList = null;
	
	
	
	private ImageView panelInfoImage;
	private TextView panelContact;
	private TextView faultTextView;
	private Button contact_engineer;
	
	private Button engineer_mode;
	

	private PanelListFragment panelListFragment;
	
	//fields to indicate current displayed and previous displayed panel and their position
	private Panel currentDisplayingPanel;
	private int currentPanelPosition = -1;
	private int previousPanelPosition = -1;
	
	
	private boolean engineerMode = false;
	
	
	private String passcodeEntered = "initial";
	
	//private int currentSelected;

	
	
	/* (non-Javadoc)callback for connection
	 * @see nlight_android.nlight.BaseActivity#receive(java.util.List, java.lang.String)
	 */
	@Override
	public void receive(List<Integer> rx, String ip) {
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		rxBuffer.addAll(rx);
		TCPConnection connection = ip_connection_map.get(ip);
		connection.setListening(true);
		System.out.println(ip + " received package: " + connection.getPanelInfoPackageNo() + " rxBuffer size: " + rxBuffer.size());
		if(connection.isRxCompleted())
		{
			//connection.closeConnection();
			//parse(ip);
			
		}
		rxBuffer.clear();
	}
	
	
	/* (non-Javadoc)
	 * @see nlight_android.nlight.InputDialogFragment.NoticeDialogListener#cancel()
	 */
	@Override
	public void cancel() {
		
		//currentDisplayingPanel = null;
		//panelListFragment.clearSelection();
		
	}
	
	/* (non-Javadoc) callback for when input dialog Enter button clicked
	 * @see nlight_android.nlight.InputDialogFragment.NoticeDialogListener#setInformation(java.lang.String)
	 */
	@Override
	public void setInformation(String input, int type) {
		System.out.println("Input information: " + input);
		
		//reset commandList
		commandList = null;
		
		//create buffer for input
		
		List<Integer> buffer = new ArrayList<Integer>();
		buffer.addAll(DataParser.convertString(input));
		System.out.println(buffer);
		
		switch(type){
			case InputDialogFragment.SET_PANEL_NAME: 
					//set current panel location and also update shared preference
					currentDisplayingPanel.setPanelLocation(input);
					savePanelToPreference();
					
					//update commandList with current buffer
					commandList = SetCmdEnum.SET_PANEL_NAME.set(buffer);
					
					//update both list and info fragments
					fragmentList.get(currentPanelPosition).updatePanelLocation(input);
					panelListFragment.updateList(currentPanelPosition, input);
					fragmentList.get(currentPanelPosition).updatePanelInfo(currentDisplayingPanel);
					break;
			case InputDialogFragment.SET_PANEL_CONTACT: 
				commandList = SetCmdEnum.SET_CONTACT_NAME.set(buffer);
					currentDisplayingPanel.setContact(input);
					fragmentList.get(currentPanelPosition).updatePanelInfo(currentDisplayingPanel);
					break;
			case InputDialogFragment.SET_PANEL_TEL: 
					commandList = SetCmdEnum.SET_CONTACT_NUMBER.set(buffer);
					currentDisplayingPanel.setTel(input);
					fragmentList.get(currentPanelPosition).updatePanelInfo(currentDisplayingPanel);
					break;
			case InputDialogFragment.SET_PANEL_MOBILE:
					commandList = SetCmdEnum.SET_CONTACT_MOBILE.set(buffer);
					currentDisplayingPanel.setMobile(input);
					fragmentList.get(currentPanelPosition).updatePanelInfo(currentDisplayingPanel);
					break;
			case InputDialogFragment.SET_PANEL_PASSCODE:
					commandList = SetCmdEnum.SET_PASSCODE.set(buffer);
					currentDisplayingPanel.setPasscode(input);
					fragmentList.get(currentPanelPosition).updatePanelInfo(currentDisplayingPanel);
					break;
			case InputDialogFragment.ENTER_PASSCODE:
					passcodeEntered = new String(input);
					
					if (input.equals(currentDisplayingPanel.getPasscode()))
					{

						panelInfoFragmentTransation(currentPanelPosition);
						currentDisplayingPanel.setEngineerMode(true);
					}
					else{
						
						faultTextView.setVisibility(View.INVISIBLE);
						panelInfoImage.setVisibility(View.INVISIBLE);
						panelContact.setVisibility(View.VISIBLE);
						panelContact.setText(getContactDetails());
						//currentDisplayingPanel = null;
						//panelListFragment.clearSelection();
						
						Toast.makeText(this, "Passcode incorrect, please contact engineer.", Toast.LENGTH_LONG).show();


					}
					//else currentDisplayingPanel = null;
					break;
					
			default: break;
		
		}
		
		//send command to panel
		setRemotePanel();
		
		
		
	}
	
	/* (non-Javadoc)
	 * @see nlight_android.nlight.PanelListFragment.OnPanelListItemClickedCallBack#onListItemClicked(java.lang.String, java.lang.String, int)
	 */
	@Override
	public void onListItemClicked(String ip, String location, int index) {
		
		
		
		
		System.out.println(location + " " +  ip + "positon: " + index);
		
		currentDisplayingPanel = panelMap.get(ip);
		
		previousPanelPosition = currentPanelPosition==-1? -1 : currentPanelPosition;
		currentPanelPosition = index;
		
		if(currentDisplayingPanel.isEngineerMode()){
			panelInfoFragmentTransation(index);
		}else{
			updatePanelInfoFragment();
			
		}
		
		int faults = currentDisplayingPanel.getFaultDeviceNo();
		if(faults>0){
			faultTextView.setText(getResources().getString(R.string.text_panelstatus_fault) + faults );
		}
		else{
			faultTextView.setText(R.string.text_panelstatus_ok);
		}
		
		
		//test for pass code dialog
		/*if(isDemo && !passcodeEntered.equals(currentDisplayingPanel.getPasscode())){
			InputDialogFragment dialog = new InputDialogFragment();
			
			//dialog.setHint("Enter passcode");
			dialog.setType(InputDialogFragment.ENTER_PASSCODE);
			dialog.show(getFragmentManager(), "inputDialog");
		} else{
			
			
			
			
		}*/
		
		updateImage();
		
	}
	
	
	
	
	
	
	//activity life circle

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panel);
		
		

		
	
		//update connection flags
		checkConnectivity();
		
		//get panelList from intent
		Intent intent = getIntent();
		panelList = intent.getParcelableArrayListExtra("panelList");
		isDemo = intent.getBooleanExtra(LoadingScreenActivity.DEMO_MODE, true);
		
		//check panelList
		if(panelList==null){
					//create demo panels if no panel list is passed in
			createDummyPanels();
						
		}
				
		//initial panel related fields
		initialFields();
		
		// save panel name to shared preference
		savePanelToPreference();
		
		
		//set panel fragments
		
		panelInfoImage = (ImageView) findViewById(R.id.panelInfo_image);
		panelContact = (TextView)findViewById( R.id.panelInfo_contact_textView);
		contact_engineer = (Button) findViewById(R.id.panel_contatc_engineer_btn);
		engineer_mode = (Button) findViewById(R.id.panel_engineer_mode_btn);
		faultTextView = (TextView) findViewById(R.id.panel_faults_textView);
		
		
		
		
		panelListFragment = (PanelListFragment) getFragmentManager().findFragmentById(R.id.fragment_panel_list); 
				
		//pass isDemo and isConnected to panelListFragment
		panelListFragment.setDemo(isDemo);
		panelListFragment.setConnected(isConnected);
		
		//set home bar back navigation to display
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		
		
		
		//set title with demo
		
		getActionBar().setTitle(isDemo? R.string.title_activity_panel_demo: R.string.title_activity_panel_live);
		
		getActionBar().setSubtitle(R.string.subtitle_activity_panel);
		
		
		
		System.out.println("DeomoMode--------> " + isDemo);
		
		
		
		
		System.out.println("All panel get: " + panelList.size());
		
		
		//if panelList exist, init FragmentList and pass panelList to PanelListFragment
		panelListFragment.setPanelList(panelList);
		
		

		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_about:
	        	
	        	Toast.makeText(this, getAppVersion(), Toast.LENGTH_SHORT).show();
	        	
	            return true;
	        case R.id.action_settings:
	            Intent intent = new Intent(this,SettingsActivity.class);
	            startActivityForResult(intent, 0);
	            return true;
	        case R.id.action_refresh:
	        	Toast.makeText(this, R.string.toast_refresh_panelStatus, Toast.LENGTH_LONG).show();
	        	panelListFragment.refreshStatus(isDemo, isConnected);
	        	return true;
	    
	        case R.id.action_show_devices:
	        	View menuItemView = findViewById(R.id.action_show_devices);
	        	showDropDownMenu(menuItemView);
	        	return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	/* (non-Javadoc) callback for pupupMenu items
	 * @see android.widget.PopupMenu.OnMenuItemClickListener#onMenuItemClick(android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch(item.getItemId())
		{
			case R.id.action_show_loops:
				System.out.println("Show Loops");
				if(currentDisplayingPanel != null && currentDisplayingPanel.isEngineerMode()){
					showDevices(currentDisplayingPanel);
				}
				return true;
			/*case R.id.action_show_faulty_devices:
				System.out.println("Show Faulty Devices");
				if(currentDisplayingPanel != null){
					panelWithFaulyDevices = Panel.getPanelWithFaulty(currentDisplayingPanel);
					showDevices(panelWithFaulyDevices);
				}
				return true;*/
        	
			default:
	            return false;
		
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		System.out.println("-----------PanelActivity onActivityResult------------");
		super.onActivityResult(requestCode, resultCode, data);
	}

	
	
	@Override
	protected void onStop() {
		
		if(panelMap!=null && ip_connection_map!=null){
			for(String key : panelMap.keySet())
			{
				
				TCPConnection connection = ip_connection_map.get(key);
				if(connection!=null){
					connection.setListening(false);
					connection.closeConnection();
					connection = null;
				}
			}
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		
		if(panelMap!=null && ip_connection_map!=null){
			for(String key : panelMap.keySet())
			{
				
				TCPConnection connection = ip_connection_map.get(key);
				if(connection!=null){
					connection.setListening(false);
					connection.closeConnection();
					connection = null;
				}
			}
		}
		
		super.onDestroy();
	}

	


	public void getAllPanels() {
		System.out.println("getAllPanels");
		
		
		
		for(String key : panelMap.keySet()){
			
			TCPConnection connection = new TCPConnection(this, key);
			
			ip_connection_map.put(key, connection);
			
		}
		
		System.out.println(ip_connection_map);
		
		List<char[]> commandList = CommandFactory.getPanelInfo();
		
		for(String key : panelMap.keySet()){
			
			TCPConnection conn = ip_connection_map.get(key);
			conn.fetchData(commandList);
		}
		
	}

	

	
	public void initialFields()
	{	
		panelMap = new HashMap<String,Panel>();
		fragmentList = new ArrayList<PanelInfoFragment>(panelList.size());
		ip_connection_map = new HashMap<String,TCPConnection>();
		rxBufferMap = new HashMap<String,List<Integer>>();
		
		
		for(int i=0; i<panelList.size();i++)
		{
			String ip = panelList.get(i).getIp();
			panelMap.put(ip, panelList.get(i));	
			PanelInfoFragment panelFragment = PanelInfoFragment.newInstance(panelList.get(i).getIp(), panelList.get(i).getPanelLocation(),panelList.get(i));
			fragmentList.add(panelFragment);
			
			
			//create connection for panels if is not in demo mode
			if(!isDemo)
			{
				ip_connection_map.put(ip, new TCPConnection(this, ip));
				rxBufferMap.put(ip, new ArrayList<Integer>());
				
			}
			
		}
			
			
	}


	public void parse(String ip){

		
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		
		List<List<Integer>> panelData = DataParser.removeJunkBytes(rxBuffer); 
		List<List<Integer>> eepRom = DataParser.getEepRom(panelData);	
		List<List<List<Integer>>> deviceList = DataParser.getDeviceList(panelData,eepRom);
		
		
		try {
			Panel newPanel = new Panel(eepRom, deviceList, ip);
			panelMap.put(ip, newPanel);
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}

	@Override
	public void passTest() {
		int temp = 0;
		
		for(String key:panelMap.keySet())
		{
			panelList.set(temp, panelMap.get(key));
			temp++;
			
		}
		
		System.out.println("Pass Panel Test");
		
		Intent intent = new Intent(this,PanelInfoActivity.class);
		intent.putParcelableArrayListExtra("panelList", (ArrayList<? extends Parcelable>) panelList);
		
		startActivity(intent);
	}
	
	private void createDummyPanels()
	{
		panelList = new ArrayList<Panel>();
		panelMap = new HashMap<String,Panel>();
		ip_connection_map = new HashMap<String,TCPConnection>();
		rxBufferMap = new HashMap<String,List<Integer>>();
		
		String ip1 = "192.168.1.17";
		panelMap.put(ip1, new Panel(ip1));
		rxBufferMap.put(ip1, new ArrayList<Integer>());
		fragmentList.add(null);
		
		String ip2 = "192.168.1.21";
		panelMap.put(ip2, new Panel(ip2));
		rxBufferMap.put(ip2, new ArrayList<Integer>());
		fragmentList.add(null);
		
		String ip3 = "192.168.1.20";
		panelMap.put(ip3, new Panel(ip3));
		rxBufferMap.put(ip3, new ArrayList<Integer>());
		fragmentList.add(null);
		
		String ip4 = "192.168.1.23";
		panelMap.put(ip4, new Panel(ip4));
		rxBufferMap.put(ip4, new ArrayList<Integer>());
		fragmentList.add(null);
	
		String ip5 = "192.168.1.24";
		panelMap.put(ip5, new Panel(ip5));
		rxBufferMap.put(ip5, new ArrayList<Integer>());
		fragmentList.add(null);
		
		for(String k: panelMap.keySet())
		{
			panelList.add(panelMap.get(k));
			
		}
		
		
	}
	
	private String getAppVersion(){
		StringBuilder version = new StringBuilder();
    	version.append("Mackwell N-Light Connect, ");
    	String app_version = getString(R.string.app_version);
    	version.append(app_version);
		
    	return version.toString();
	}
	
	private void showDevices(Panel panel){
		System.out.println("Get Device List");
		
		Intent intent = new Intent(this, DeviceActivity.class);

		if(panel!=null){
			
			intent.putExtra("location", panel.getPanelLocation());
			intent.putExtra("panel", panel);
			intent.putExtra("loop1",panel.getLoop1());
			intent.putExtra("loop2",panel.getLoop2());
			intent.putExtra(LoadingScreenActivity.DEMO_MODE, isDemo);
			startActivity(intent);
			
		}
		
	}

	private void savePanelToPreference()
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isSave = sp.getBoolean(SettingsActivity.SAVE_PANEL_LOCATION, true);
		if(isSave)
		{
			for(Panel panel : panelList)
			{
				SharedPreferences.Editor editor = sp.edit();
				editor.putString(panel.getIp(), panel.getPanelLocation());
				editor.commit();
			}
			
		};
		
		
		
	}
	
	public void showDropDownMenu(View view)
	{
		System.out.println("Panel Drop Down Menu");
		PopupMenu popup = new PopupMenu(this, view);
		popup.setOnMenuItemClickListener(this);
	    MenuInflater inflater = popup.getMenuInflater();
	    inflater.inflate(R.menu.show_devices, popup.getMenu());
	    popup.show();
	}

	/**
	 *  Send command to live panel, if it is in live mode
	 */
	private void setRemotePanel(){
		
		checkConnectivity();
		if(currentDisplayingPanel!=null){
			String ip = currentDisplayingPanel.getIp();
			TCPConnection conn = ip_connection_map.get(ip);
				
			if(!isDemo &&  conn != null && commandList != null){
				conn.fetchData(commandList);
			}
		
			commandList = null;
		}
		
	}
	
	private void panelInfoFragmentTransation(int index){
		panelInfoImage.setVisibility(View.INVISIBLE);
		panelContact.setVisibility(View.INVISIBLE);
		faultTextView.setVisibility(View.INVISIBLE);
		
		
		String ip = currentDisplayingPanel.getIp();
		if(panelMap.get(ip)==null)
		{
			panelMap.put(ip, new Panel(ip));
		}
		
		
		
		if(fragmentList.get(index) == null)
		{
			PanelInfoFragment panelFragment = PanelInfoFragment.newInstance(ip, currentDisplayingPanel.getPanelLocation(),panelMap.get(ip));
			
			fragmentList.set(index, panelFragment);
		}
		
		
		
		
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		
		fragmentTransaction.replace(R.id.panel_detail_container, fragmentList.get(index),"tagTest");
		//fragmentTransaction.addToBackStack(null);  add fragment to backstack
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.commit();
		
		contact_engineer.setVisibility(View.INVISIBLE);
		engineer_mode.setVisibility(View.INVISIBLE);


	}
	
	/**
	 * Remove PanelInfoFagment and reset Mackwell logo 
	 */
	private void updatePanelInfoFragment(){
		
			panelInfoImage.setVisibility(View.VISIBLE);
			faultTextView.setVisibility(View.VISIBLE);
		
			panelContact.setVisibility(View.INVISIBLE);
		
			FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		
			//get previous displayed fragment
			if(previousPanelPosition!=-1) {
				fragmentTransaction.remove(fragmentList.get(previousPanelPosition));
			}	
		
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentTransaction.commit();
			
			contact_engineer.setVisibility(View.VISIBLE);
			engineer_mode.setVisibility(View.VISIBLE);
		
		
	}
	
	
	private void updateImage(){
		
		switch(currentDisplayingPanel.getOverAllStatus()){
		
			case Panel.OK:	
				panelInfoImage.setImageResource(R.drawable.greentick);
				break;
			case Panel.FAULT:  
				panelInfoImage.setImageResource(R.drawable.redcross);
				break;
			default: break;
		}
		
		
	}
	
	public void contactEngineerBtn(View view){
		
		
		if(currentDisplayingPanel!=null && !currentDisplayingPanel.isEngineerMode()){
			panelInfoImage.setVisibility(panelInfoImage.isShown()? 4:0);
			faultTextView.setVisibility(faultTextView.isShown()? 4:0);
			panelContact.setVisibility(panelInfoImage.isShown()? 4:0);
			panelContact.setText(getContactDetails());
		}
		
	}

	/**
	 * When engineerModeBtn clicked
	 * @param view
	 */
	public void engineerModeBtn(View view){
		
		
		if(currentDisplayingPanel!=null && !currentDisplayingPanel.isEngineerMode()){
			InputDialogFragment dialog = new InputDialogFragment();
		
			//dialog.setHint("Enter passcode");
			dialog.setType(InputDialogFragment.ENTER_PASSCODE);
			dialog.show(getFragmentManager(), "inputDialog");
			
			
		} 
		
		
		
	}
	
	/**
	 * Using a StringBuilder to build a string for contact textview
	 * @return contact details string
	 */
	private String getContactDetails(){
		StringBuilder sb = new StringBuilder();
		sb.append(getResources().getString(R.string.text_contat_engineer) + "\n");
		sb.append(getResources().getString(R.string.text_control_panel_location) + currentDisplayingPanel.getPanelLocation() + "\n");
		sb.append(getResources().getString(R.string.text_contact_name) + currentDisplayingPanel.getContact()+ "\n");
		sb.append(getResources().getString(R.string.text_contact_tel) + currentDisplayingPanel.getTel() + "\n");
		sb.append(getResources().getString(R.string.text_contact_mobile) + currentDisplayingPanel.getMobile());
		
		return sb.toString();
	}
	
		
}
