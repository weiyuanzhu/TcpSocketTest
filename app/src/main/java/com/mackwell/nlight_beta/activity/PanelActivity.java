package com.mackwell.nlight_beta.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.mackwell.nlight_beta.R;
import com.mackwell.nlight_beta.models.Panel;
import com.mackwell.nlight_beta.activity.InputDialogFragment.NoticeDialogListener;
import com.mackwell.nlight_beta.activity.PanelListFragment.OnPanelListItemClickedCallBack;
import com.mackwell.nlight_beta.socket.TcpLongConnection;
import com.mackwell.nlight_beta.util.CommandFactory;
import com.mackwell.nlight_beta.util.DataHelper;
import com.mackwell.nlight_beta.util.MySQLiteController;
import com.mackwell.nlight_beta.util.SetCmdEnum;

/**
 * @author weiyuan zhu
 *
 */
public class PanelActivity extends BaseActivity implements OnPanelListItemClickedCallBack, TcpLongConnection.CallBack, PopupMenu.OnMenuItemClickListener, NoticeDialogListener{

    private static final String TAG = "PanelActivity";

    private static final int REQUEST_PANEL = 1;

    private android.os.Handler mHandler;
    private boolean splitScreen = false;

	private List<Panel> panelList = null;
	private Map<String,Panel> panelMap = null;
	private Map<String,TcpLongConnection> ip_connection_map = null;
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

    //database
    private MySQLiteController sqLiteController;



	/* (non-Javadoc)callback for connection
	 * @see nlight_android.nlight.BaseActivity#receive(java.util.List, java.lang.String)
	 */
	@Override
	public void receive(List<Integer> rx, String ip) {
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		rxBuffer.addAll(rx);
		TcpLongConnection connection = ip_connection_map.get(ip);
		connection.setListening(true);
		System.out.println(ip + " received package: " + connection.getPanelInfoPackageNo() + " rxBuffer size: " + rxBuffer.size());
		if(connection.isRxCompleted())
		{
			//connection.closeConnection();
			//parse(ip);
			//todo
		}

		rxBuffer.clear();
	}


    @Override
    public void onError(String ip, Exception e) {
        if (e instanceof TcpLongConnection.PanelResetException) {
            mHandler.post(panelResetError);
        }

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
		buffer.addAll(DataHelper.convertString(input));
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

						panelInfoFragmentTransaction(currentPanelPosition);
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
		
		
		
		
		System.out.println(location + " " +  ip + "position: " + index);

        // In split screen mode, show the detail view in this activity by
        // adding or replacing the detail fragment using a
        // fragment transaction.

        if(splitScreen) {
            currentDisplayingPanel = panelMap.get(ip);

            previousPanelPosition = currentPanelPosition == -1 ? -1 : currentPanelPosition;
            currentPanelPosition = index;

            if (currentDisplayingPanel.isEngineerMode()) {
                panelInfoFragmentTransaction(index);
            } else {
                updatePanelInfoFragment();

            }

            int faults = currentDisplayingPanel.getFaultDeviceNo();
            if (faults > 0) {
                faultTextView.setText(getResources().getString(R.string.text_panelstatus_fault) + faults);
            } else {
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
        else{
            //navigate to panelInfoActivity;
            currentDisplayingPanel = panelMap.get(ip);

            Intent intent = new Intent(this,PanelInfoActivity.class);
            intent.putExtra("panel",currentDisplayingPanel);
            startActivity(intent);

        }
		
	}
	
	
	
	
	
	
	//activity life circle

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.panel, menu);
		return true;
	}


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);

        mHandler = new android.os.Handler();  //handler

        sqLiteController = new MySQLiteController(this); //sql controller



        // The detail container view will be present only in the
        // large-screen landscape layouts (res/values-w720dp).
        // If this view is present, then the
        // activity should be in split screen mode.

        if(findViewById(R.id.panel_detail_container)!=null)
        {
            //flag true for split screen
            splitScreen = true;

            //set screen orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);

            //set panel fragments
            panelInfoImage = (ImageView) findViewById(R.id.panelInfo_image);
            panelContact = (TextView)findViewById( R.id.panelInfo_contact_textView);
            contact_engineer = (Button) findViewById(R.id.panel_contatc_engineer_btn);
            engineer_mode = (Button) findViewById(R.id.panel_engineer_mode_btn);
            faultTextView = (TextView) findViewById(R.id.panel_faults_textView);

        }



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

        //set home bar back navigation to display
        if (getActionBar()!=null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }


        panelListFragment = (PanelListFragment) getFragmentManager().findFragmentById(R.id.fragment_panel_list);

        //pass isDemo and isConnected to panelListFragment
        panelListFragment.setDemo(isDemo);
        panelListFragment.setConnected(isConnected);


        //set title with demo

        getActionBar().setTitle(isDemo? R.string.title_activity_panel_demo: R.string.title_activity_panel_live);

        getActionBar().setSubtitle(R.string.subtitle_activity_panel);

        System.out.println("DemoMode--------> " + isDemo);

        System.out.println("All panel get: " + panelList.size());


        //if panelList exist, init FragmentList and pass panelList to PanelListFragment
        panelListFragment.setPanelList(panelList);
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
                if (currentDisplayingPanel != null) {
                    View menuItemView = findViewById(R.id.action_show_devices);
                    showDropDownMenu(menuItemView);
                } else {
                    Toast.makeText(this,getResources().getString(R.string.toast_select_panel),Toast.LENGTH_SHORT).show();
                }
                return true;
            case android.R.id.home:
                intent = NavUtils.getParentActivityIntent(this);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	/* (non-Javadoc) callback for popupMenu items
	 * @see android.widget.PopupMenu.OnMenuItemClickListener#onMenuItemClick(android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch(item.getItemId())
		{
			case R.id.action_show_loops:
				System.out.println("Show Loops");
                if (currentDisplayingPanel != null && currentDisplayingPanel.isEngineerMode()) {
                    showDevices(currentDisplayingPanel);
                } else {
                    Toast.makeText(this,getResources().getString(R.string.toast_enter_engineer_mode),Toast.LENGTH_SHORT).show();
                }

				return true;
			case R.id.action_show_report:
				Log.i(TAG,"Show report");
                if (currentDisplayingPanel != null) {
                    Intent intent = new Intent(this, ReportActivity.class);
                    intent.putExtra("ip",currentDisplayingPanel.getIp());
                    intent.putExtra("location",currentDisplayingPanel.getPanelLocation());
                    intent.putExtra("demo",isDemo);
                    startActivity(intent);
                } else {
                    ReportFragment fragment = ReportFragment.newInstance("arg1","arg2");

                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.panel_detail_container,fragment);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commit();
                }

				return true;
        	
			default:
	            return false;
		
		}
	}


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Panel panel = intent.getParcelableExtra("panel");
        String ip = intent.getStringExtra("ip");

        currentDisplayingPanel = panel;
        panelMap.put(ip, panel);
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("-----------PanelActivity onActivityResult------------");

        //result from device activity
        if (requestCode==REQUEST_PANEL && resultCode == Activity.RESULT_OK) {
            Panel panel = data.getParcelableExtra("panel");
            String ip = data.getStringExtra("ip");


            currentDisplayingPanel = panel;
            panelMap.put(ip, panel);

        }
	}


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("PanelActivity","onResume");

//      set panelInfoImage's icon address
        String imageLocation = sharedPreferences.getString("pref_app_icons","default image");
        boolean customIcon = sharedPreferences.getBoolean("pref_icon_checkbox", false);
        Uri uri = Uri.parse(imageLocation);


//      when split screen and no panel selected and result has a valid file path
        if (currentPanelPosition !=-1) {
             //do nothing
        }else if(customIcon && !imageLocation.equals("default image") && splitScreen && currentPanelPosition==-1)  {
            panelInfoImage.setImageURI(uri);

        }
        else{
            panelInfoImage.setImageResource(R.drawable.mackwell_logo);
        }





    }



    @Override
	protected void onStop() {
		
		if(panelMap!=null && ip_connection_map!=null){
			for(TcpLongConnection connection : ip_connection_map.values())
			{
				if(connection!=null){
					connection.setListening(false);
					connection.closeConnection();
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

				TcpLongConnection connection = ip_connection_map.get(key);
				if(connection!=null){
					connection.setListening(false);
					connection.closeConnection();
//					connection = null;
				}
			}
		}
		
		super.onDestroy();
	}

	


	public void getAllPanels() {
		System.out.println("getAllPanels");
		
		
		
		for(String key : panelMap.keySet()){
			
			TcpLongConnection connection = new TcpLongConnection(this, key);
			
			ip_connection_map.put(key, connection);
			
		}
		
		System.out.println(ip_connection_map);
		
		List<char[]> commandList = CommandFactory.getPanelInfo();
		
		for(String key : panelMap.keySet()){
			
			TcpLongConnection conn = ip_connection_map.get(key);
			conn.fetchData(commandList);
		}
		
	}

	

	
	void initialFields()
	{	
		panelMap = new HashMap<String,Panel>();
		fragmentList = new ArrayList<PanelInfoFragment>(panelList.size());
		ip_connection_map = new HashMap<String,TcpLongConnection>();
		rxBufferMap = new HashMap<String,List<Integer>>();


        for (Panel aPanel : panelList) {
            String ip = aPanel.getIp();
            panelMap.put(ip, aPanel);
            PanelInfoFragment panelFragment = PanelInfoFragment.newInstance(aPanel.getIp(), aPanel.getPanelLocation(), aPanel);
            fragmentList.add(panelFragment);


            //create connection for panels if is not in demo mode
            if (!isDemo) {
                ip_connection_map.put(ip, new TcpLongConnection(this, ip));
                rxBufferMap.put(ip, new ArrayList<Integer>());

            }

        }
			
			
	}


	public void parse(String ip){

		
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		
		List<List<Integer>> panelData = DataHelper.removeJunkBytes(rxBuffer);
		List<List<Integer>> eepRom = DataHelper.getEepRom(panelData);
		List<List<List<Integer>>> deviceList = DataHelper.getDeviceList(panelData, eepRom);
		
		
		try {
			Panel newPanel = panelMap.get(ip);
            newPanel.updatePanel(eepRom, deviceList, ip);
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
		ip_connection_map = new HashMap<String,TcpLongConnection>();
		rxBufferMap = new HashMap<String,List<Integer>>();
		
		String ip1 = "192.168.1.17";
		panelMap.put(ip1, new Panel(ip1,"0:0:0:0:0:0"));
		rxBufferMap.put(ip1, new ArrayList<Integer>());
		fragmentList.add(null);
		
		String ip2 = "192.168.1.21";
		panelMap.put(ip2, new Panel(ip2,"0:0:0:0:0:0"));
		rxBufferMap.put(ip2, new ArrayList<Integer>());
		fragmentList.add(null);
		
		String ip3 = "192.168.1.20";
		panelMap.put(ip3, new Panel(ip3,"0:0:0:0:0:0"));
		rxBufferMap.put(ip3, new ArrayList<Integer>());
		fragmentList.add(null);
		
		String ip4 = "192.168.1.23";
		panelMap.put(ip4, new Panel(ip4,"0:0:0:0:0:0"));
		rxBufferMap.put(ip4, new ArrayList<Integer>());
		fragmentList.add(null);
	
		String ip5 = "192.168.1.24";
		panelMap.put(ip5, new Panel(ip5,"0:0:0:0:0:0"));
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
			startActivityForResult(intent,REQUEST_PANEL);
//			startActivity(intent);
		}
		
	}

	private void savePanelToPreference()
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isSave = sp.getBoolean(SettingsActivity.SAVE_PANEL_LOCATION, true);
		if(isSave)
		{
            sqLiteController.open();
			for(Panel panel : panelList)
			{
                //save panel location to SharedPreference
				SharedPreferences.Editor editor = sp.edit();
				editor.putString(panel.getIp(), panel.getPanelLocation());
				editor.apply();

                //save panel location to database
                //if(!isDemo) sqLiteController.insertPanel(panel); //put demo panel into list for test

                sqLiteController.updatePanelLocation(panel.getMacString(), panel.getPanelLocation());

            }
            sqLiteController.close();
        }
	}
	
	void showDropDownMenu(View view)
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
			TcpLongConnection conn = ip_connection_map.get(ip);
				
			if(!isDemo &&  conn != null && commandList != null){
				conn.fetchData(commandList);
			}
		
			commandList = null;
		}
		
	}
	
	private void panelInfoFragmentTransaction(int index){
		panelInfoImage.setVisibility(View.INVISIBLE);
		panelContact.setVisibility(View.INVISIBLE);
		faultTextView.setVisibility(View.INVISIBLE);
		
		
		String ip = currentDisplayingPanel.getIp();
		if(panelMap.get(ip)==null)
		{
			panelMap.put(ip, new Panel(ip,"0:0:0:0:0:0"));
		}
		
		
		
		if(fragmentList.get(index) == null)
		{
			PanelInfoFragment panelFragment = PanelInfoFragment.newInstance(ip, currentDisplayingPanel.getPanelLocation(),panelMap.get(ip));
			
			fragmentList.set(index, panelFragment);
		}

		
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		
		fragmentTransaction.replace(R.id.panel_detail_container, fragmentList.get(index),"tagTest");
		//fragmentTransaction.addToBackStack(null);  add fragment to back stack
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.commit();
		
		contact_engineer.setVisibility(View.INVISIBLE);
		engineer_mode.setVisibility(View.INVISIBLE);

	}
	
	/**
	 * Remove PanelInfoFragment and reset Mackwell logo
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
			panelInfoImage.setVisibility(panelInfoImage.isShown()? View.INVISIBLE:View.VISIBLE);
			faultTextView.setVisibility(faultTextView.isShown()? View.INVISIBLE:View.VISIBLE);
			panelContact.setVisibility(panelInfoImage.isShown()? View.INVISIBLE:View.VISIBLE);
			panelContact.setText(getContactDetails());
		}
		
	}

	/**
	 * When engineerModeBtn clicked
	 * @param view button
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
	 * Using a StringBuilder to build a string for contact text view
	 * @return contact details string
	 */
	private String getContactDetails(){
		StringBuilder sb = new StringBuilder();
		sb.append(getResources().getString(R.string.text_contat_engineer) + "\n");
		sb.append(getResources().getString(R.string.text_control_panel_location)+ "        " + currentDisplayingPanel.getPanelLocation() + "\n");
		sb.append(getResources().getString(R.string.text_contact_name) + "                      " + currentDisplayingPanel.getContact()+ "\n");
		sb.append(getResources().getString(R.string.text_contact_tel) + "                               " + currentDisplayingPanel.getTel() + "\n");
		sb.append(getResources().getString(R.string.text_contact_mobile) + "                                   " + currentDisplayingPanel.getMobile());
		
		return sb.toString();
	}

   final Runnable panelResetError = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(PanelActivity.this,R.string.toast_panel_reset, Toast.LENGTH_LONG).show();

            //force navigate back to loading screen
            /*Intent intent = new Intent(DeviceActivity.this,LoadingScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            */
        }
    };
	
		
}
