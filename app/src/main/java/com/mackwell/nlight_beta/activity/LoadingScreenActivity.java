package com.mackwell.nlight_beta.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mackwell.nlight_beta.R;
import com.mackwell.nlight_beta.models.Device;
import com.mackwell.nlight_beta.models.Panel;
import com.mackwell.nlight_beta.socket.PanelConnection;
import com.mackwell.nlight_beta.socket.UDPConnection;
import com.mackwell.nlight_beta.socket.UDPConnection.UDPCallback;
import com.mackwell.nlight_beta.util.CommandFactory;
import com.mackwell.nlight_beta.util.DataParser;
import com.mackwell.nlight_beta.util.MySQLiteController;

/**
 * @author  weiyuan zhu15/04/2014 Starting develop branch test on develop branch test 2 on feature branch test 3 on feature branch after rebase
 */

public class LoadingScreenActivity extends BaseActivity implements PanelConnection.CallBack,ListDialogFragment.ListDialogListener, UDPCallback{
	
	private static final String TAG = "LoadingScreen";
    public static final String DEMO_MODE = "Demo Mode";

	//ipListAll = new String[] {"192.168.1.17","192.168.1.20","192.168.1.21","192.168.1.23","192.168.1.24"};
	private ArrayList<String> ipListAll = null;
    private Map<String,Boolean> ipEnableMap = null;
	private ArrayList<String> ipListSelected = null;
	
	private static final int LOADING = 0;
	private static final int PARSING = 1;
	private static final int LOADING_FINISHED = 2;
	private static final int ERROR = 3;
	
	private boolean isLoading = false;
	
	private Button liveBtn = null;
	private Button demoBtn = null;
	private TextView progressText = null;
	private ProgressBar progressBar = null;
	private int progress = 0;
	
	private List<Panel> panelList = null;
	private Map<String,Panel> panelMap = null;
	private Map<String,PanelConnection> ip_connection_map = null;
	private Map<String,List<Integer>> rxBufferMap = null;

    //database
    MySQLiteController sqLiteController;
	
	private UDPConnection udpConnection = null;
	List<Map<String, Object>> dataList = null; // datalist for panel list dialog
	
	private static int delay = 1000;
	private Handler mHandler = null;
	
	private int panelToLoad = 0; 
	
	

	/* (non-Javadoc) implement TCPcallback, receiving data package
	 * @see mackwell.nlight.BaseActivity#receive(java.util.List, java.lang.String)
	 */
	public void receive(List<Integer> rx, String ip) {
		
		Message msg = mHandler.obtainMessage();
		progress++;
		
		msg.arg1 = LOADING;
		msg.arg2 = progress;
		
		
		
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		rxBuffer.addAll(rx);
		PanelConnection connection = ip_connection_map.get(ip);
		connection.setListening(false);
		System.out.println(ip + " received package: " + connection.getPanelInfoPackageNo() + " rxBuffer size: " + rxBuffer.size());
		if(connection.isRxCompleted())
		{
			panelToLoad--;

			//update progress with handler
			
			
			if(panelToLoad==0){
				msg.arg1 = PARSING;
				
			}
			
			
			//mHandler.sendMessage(msg);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			parse(ip);
			
		}
		
		mHandler.sendMessage(msg);
		
	}

    @Override
    public void onError(String ip,Exception e) {

        isLoading = false;

        //set ip checkbox disable
        ipEnableMap.put(ip,false);
        panelMap.get(ip).setError(true);

        System.out.println("Error: " + ip);
        if(ip_connection_map.get(ip)!=null) {
            ip_connection_map.get(ip).closeConnection();
            ip_connection_map.get(ip).setListening(false);
        }

        //stop all other connections
        for(PanelConnection connection1:ip_connection_map.values())
        {
            connection1.setError(true);
        }

        Message msg = mHandler.obtainMessage();
        msg.arg1 = ERROR;
        msg.obj = ip;
        mHandler.sendMessage(msg);

//		ipListAll.remove(ip);
        panelToLoad	--;
    }

	/* (non-Javadoc) implementing UDPcallback
	 * @see nlight_android.socket.UDPConnection.UDPCallback#addIp(java.lang.String)
	 */
	public int addIp(byte[] mac,String ip)
	{
		System.out.println("Received UDP package");
		if(!ipListAll.contains(ip))
		{
			//ipListAll.add(ip);

            //get macString from byte[]
            String macString = String.format("%02X:%02X:%02X:%02X:%02X:%02X",
                    mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);

            //create new panel and put it in the panel map
            Panel panel = new Panel(ip,macString);
           // panelMap.put(ip,panel);
            sqLiteController.open();
            sqLiteController.insertPanel(panel);
            sqLiteController.close();

            ipEnableMap.put(ip,true);
			
			//put ip and location into a map and add to dataList for dialog listview;
			//Map<String, Object> map = new HashMap<String,Object>();
			//map.put("ip", ip);
			//map.put("location",getPanelLocationFromPreference(ip));
			//dataList.add(map);
			
			return 0;
		}
		return 1;
	}

    /*
     * implement ListDialogFragment's ListDialogFragment's ListDialogListener interface
     * @see nlight_android.activity.ListDialogFragment.ListDialogListener#searchAgain();
     */
	@Override
    public void searchAgain() {

        //reset all items in ipEnableMap to true
        if(ipEnableMap!=null) ipEnableMap.clear();

        for(Panel panel: panelMap.values()){
            panel.setError(false);
        }

		searchUDP();			
	}
	 
	/* (non-Javadoc) implementing ListDialogFragment's ListDialogListener interface
	 * @see nlight_android.activity.ListDialogFragment.ListDialogListener#connectPanels(java.util.List)
	 */
	@Override
	public void connectToPanels(List<Integer> selected) {
		
		//connecting to panels, and close UDP socket
		if(udpConnection!=null) {
            udpConnection.setListen(false);
        }

        //clear panel list from previous loading
        panelList.clear();
		
		//save check status
		savePanelSelectionToIpListSelected(selected);
		
		progressBar.setMax(16*ipListSelected.size());
        progress = 0;
		
		
		System.out.println(ipListSelected);
		
		
		
		
		/*
		 *  Initial connections and rxBuffer for each panel
		 */
		
		for(String ip : ipListSelected)
		{
			PanelConnection connection = new PanelConnection(this, ip);
			ip_connection_map.put(ip, connection);
			rxBufferMap.put(ip, new ArrayList<Integer>());
			
		}
		//on main ui thread
		//show progress bar and text
		
		//set isDemo flag
		isDemo = false;
		panelToLoad = ipListSelected.size();

		//check if loading is not already in process and panel selected not equal to 0
		if(!isLoading && ipListSelected.size()!=0){

           progressText.setText(getResources().getString(R.string.text_loading_panel,panelToLoad));

            //reset progress bar and status text
			progressText.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress);
			
			
			System.out.println("------------liveMode clicked");
			List<char[]> commandList = CommandFactory.getPanelInfo();
			
			for(String ip: ipListSelected){

                ip_connection_map.get(ip).fetchData(commandList);
			}
			
			
			//set button disable
			liveBtn.setEnabled(false);
			demoBtn.setEnabled(false);
			isLoading = true;
			
		}
		
		saveCheckedStatsToPreference();
		
	}
	
	/* (non-Javadoc) implementing ListDialogFragment's ListDialogListener interface
	 * @see nlight_android.nlight.ListDialogFragment.ListDialogListener#cancelDialog(java.util.List)
	 */
	@Override
	public void cancelDialog(List<Integer> selected) {
		
		savePanelSelectionToIpListSelected(selected);
		saveCheckedStatsToPreference();
		
	}

	
	/**
	 * This function takes an ip for the panel and return it's location in the SharedPreference 
	 * @param ip IP address for the panel
	 * @return location for the panel, return "" if not save
	 */
	private String getPanelLocationFromPreference(String ip)
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean savePanelLocation = sp.getBoolean(SettingsActivity.SAVE_PANEL_LOCATION, true);
		
		if(savePanelLocation)
		{
			//return cached panel location
			return sp.getString(ip, "");
		}
		
		else return "";
	}
	
	
	
	//life Cycle
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading_screen);
		
		//init sqlite controller
        sqLiteController = new MySQLiteController(this);
		
		//init all view items
		liveBtn = (Button) findViewById(R.id.loadscreen_live_imageBtn);
		demoBtn = (Button) findViewById(R.id.loadscreen_demo_imageBtn);
		progressText = (TextView) findViewById(R.id.loadscreen_progress_textView);
		progressBar = (ProgressBar) findViewById(R.id.loadscreen_progressBar);
		
		//init loading panals 
		initializeFields();
		
		//search for all panels
		searchUDP();
		
		//update connection flags
		checkConnectivity();
		
		//handler for deal with UI update and navigation
		mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				
				progressBar.setProgress(msg.arg2);
				
				switch(msg.arg1){
					case LOADING:
                        if(isLoading){
                            progressText.setText(getResources().getString(R.string.text_loading_panel,panelToLoad));
                        }
						break;
					case PARSING: 
						progressText.setText(R.string.text_analyzing_data);
						break;
					case LOADING_FINISHED:
						progressText.setText(R.string.text_finish_loading);
						break;
					case ERROR:
						String ipAdd = (String) msg.obj;
						progressText.setText(getResources().getString(R.string.text_connect_error,ipAdd));
						progressBar.setVisibility(View.INVISIBLE);
						liveBtn.setEnabled(true);
                        demoBtn.setEnabled(true);
						break;
					default: 
						
						break;
					
					
				
				}
				
				super.handleMessage(msg);
			}
			
			
		};
		
		
		
		/*
		//create a new udpConnection instance, if it exist, then close previous udp connnection
		if(udpConnection == null ){
			udpConnection = new UDPConnection(Constants.FIND_PANELS, this);
		}
		else
		{
			udpConnection.closeConnection();
			udpConnection = new UDPConnection(Constants.FIND_PANELS,this);
			
		}
		
		//send UDP panel search messages
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(udpConnection);
		exec.shutdown();
		*/
		
	}
	
	//when activity moves to background
	protected void onStop()
	{
		super.onStop();
		
		//close UDP connection
		/*if(udpConnection!=null)
		{
			udpConnection.setListen(false);
			udpConnection.closeConnection();
		}
		
		//close TCP connections
		for(String key : ip_connection_map.keySet())
		{
			
			ip_connection_map.get(key).closeConnection();
            ip_connection_map.get(key) = null;
		}*/
		
	}

	@Override
	protected void onResume() {
		
		Log.d(TAG,"onResume");
		super.onResume();

        //reset panel elements if no panel is being loading
        if (!isLoading) {
            //reset panelToLoad and clear panelList to prevent unexpected onError
            panelToLoad = 0;
            panelList.clear();

            //reset progress bar and progress
            progress = 0;

            progressBar.setVisibility(View.INVISIBLE);

            //re-enable buttons
            demoBtn.setEnabled(true);
            liveBtn.setEnabled(true);

            //hide bars
            progressText.setVisibility(View.INVISIBLE);
            //progressBar.setVisibility(View.INVISIBLE);
        }

    }


	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		
		//close UDP connection
		if(udpConnection!=null)
		{
			udpConnection.setListen(false);
			udpConnection.closeConnection();
		}
		
		//close TCP connections
		for(String key : ip_connection_map.keySet())
		{
//			PanelConnection connection = ip_connection_map.get(key);
            ip_connection_map.get(key).closeConnection();
//			connection = null;
		}
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.loading_screen, menu);
		return true;
	}

	//pass all panel objects to next activity when loading and parsing are finished
	Runnable loadFinished = new Runnable()
	{

		@Override
		public void run() {

            //close UDP connection
            if(udpConnection!=null){
                udpConnection.closeConnection();
                udpConnection = null;
            }
			isLoading = false;

			//create intent
			Intent intent = new Intent(LoadingScreenActivity.this, PanelActivity.class);
			
			//put panelList into intent
			intent.putParcelableArrayListExtra("panelList", (ArrayList<? extends Parcelable>) panelList);
			intent.putExtra(DEMO_MODE, isDemo);
			startActivity(intent);

			//clear ipSelected list 
			ipListSelected.clear();

			//finish this activity to prevent back navi
			//finish();
		}
	};

    /*
		 * Initial fields
		 *
		 */
	public void initializeFields()
	{

		ipListAll = new ArrayList<String>();
        ipEnableMap = new HashMap<String, Boolean>();
		ipListSelected = new ArrayList<String>();
		
		
		//paneList(Parcelable) is for navigation
		panelList = new ArrayList<Panel>();
		
		panelMap = new HashMap<String,Panel>();
		ip_connection_map = new HashMap<String,PanelConnection>();
		rxBufferMap = new HashMap<String,List<Integer>>();
		
		dataList = new ArrayList<Map<String,Object>>();
		
	}
	
	public void demoMode(View view){
		
		//set isDemo flag
		demoBtn.setEnabled(false);
		
		isDemo = true;
		
		progressText.setText(R.string.text_prepPanelData);
		progressText.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);

		prepareDataForDemo();
		
		mHandler.postDelayed(loadFinished, delay);
		
	}
	
	
	public void liveMode(View view)
	{
		/*if(ipListAll.size()==0){
			
			searchUDP();
		}*/

//        panelMap.clear();
        dataList.clear();
        ipEnableMap.clear();

        //compare current ipListAll with cached sqLite database
        sqLiteController.open();
        Cursor cursor = sqLiteController.selectIp();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String ip = cursor.getString(0);
            String macString = cursor.getString(1);
            String location = cursor.getString(2);

            if(!ipListAll.contains(ip)) ipListAll.add(ip);

            if(panelMap.get(ip)==null){
                Panel panel = new Panel(ip,macString);
                panelMap.put(ip,panel);
                ipEnableMap.put(ip,true);

            }
            else ipEnableMap.put(ip,!panelMap.get(ip).isError());



            //put ip and location into a map and add to dataList for dialog listview;
            Map<String, Object> map = new HashMap<String,Object>();
            map.put("ip", ip);
            map.put("location",location);
            dataList.add(map);

        }



        cursor.close();
        sqLiteController.close();

        //create a new ListDialogFragment and set its String[] ips to be udp search result
        ListDialogFragment panelListDialog = new ListDialogFragment();

        //get a String[] from ipSet and pass to dialog window
        String[] ipArray = new String[ipListAll.size()];
        ipListAll.toArray(ipArray);
        panelListDialog.setIps(ipArray);
        panelListDialog.setDataList(dataList);
        panelListDialog.setIpEnableMap(ipEnableMap);

        //test.setIps(null); //null test
        panelListDialog.show(getFragmentManager(), "panelListDialog"); //popup dialog

	}
	
	public void parse(String ip)
	{
		
		
		Message msg = mHandler.obtainMessage();
		//msg.arg1 = PARSING;
		//mHandler.sendMessage(msg);
		
		List<Integer> rxBuffer = rxBufferMap.get(ip);
		
		List<List<Integer>> panelData = DataParser.removeJunkBytes(rxBuffer); 
		List<List<Integer>> eepRom = DataParser.getEepRom(panelData);	
		List<List<List<Integer>>> deviceList = DataParser.getDeviceList(panelData,eepRom);
		
		
		try {
			Panel panel = panelMap.get(ip);

            panel.updatePanel(eepRom, deviceList, ip);
			panelMap.put(ip, panel);
			panelList.add(panel);
			
			if(panelList.size()==ipListSelected.size()){
				
				//msg = new Message();
				msg.arg1 = LOADING_FINISHED;
				mHandler.sendMessage(msg);

				mHandler.postDelayed(loadFinished, delay);	
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void prepareDataForDemo()
	{
		panelList = new ArrayList<Panel>();
	
		Panel panel = new Panel("192.168.1.241","00:00:00:00:00:01");
		panel.setPanelLocation("TEST Mackwell L&B 1   ");
		panel.setSerialNumber((long)1376880756);
		panel.setGtinArray(new int[]{131,1,166,43,154,4});
		panel.setReportUsageLong(10234);

		panel.getLoop1().addDevice(new Device(0,true,"?LB 1",0,0,0,254,1375167879,new int[]{11,1,166,43,154,4}));
		panel.getLoop1().addDevice(new Device(1,true,"LB 2",0,0,0,200,1374967295,new int[]{45,2,166,43,154,4}));
		
		
		panel.getLoop2().addDevice(new Device(128,true,"LB 4",0,0,0,150,1374467255,new int[]{78,3,166,43,154,4}));
		panel.getLoop2().addDevice(new Device(129,true,"LB 4",0,0,0,150,1374537221,new int[]{130,4,166,43,154,4}));
		
		
		panelList.add(panel);
	
		panel = new Panel("192.168.1.242","00:00:00:00:00:02");
		panel.setPanelLocation("TEST Mackwell L&B 2    ");
		panel.setSerialNumber((long)1375868516);
		panel.setGtinArray(new int[]{132,2,166,43,154,4});
		panel.setReportUsageLong(1010234);
		
		panel.getLoop1().addDevice(new Device(0,true,"LB 5",0,2,0,0,1365167879,new int[]{145,5,166,43,154,4}));
		panel.getLoop1().addDevice(new Device(1,true,"LB 6",0,6,0,200,1366965291,new int[]{178,5,166,43,154,4}));
		panel.getLoop1().addDevice(new Device(2,true,"?",4,2,2,0,1374967295,new int[]{45,2,166,43,154,4}));
		panel.getLoop1().addDevice(new Device(3,true,"Dining Room",8,0,0,200,1374967295,new int[]{45,2,166,43,154,4}));
		panel.getLoop1().addDevice(new Device(4,true,"Bed Room",64,0,0,192,1374965293,new int[]{200,1,162,43,154,4}));
		
		
		panel.getLoop2().addDevice(new Device(128,true,"LB 7",0,0,0,150,1361562293,new int[]{223,3,166,43,154,4}));
		panel.getLoop2().addDevice(new Device(129,true,"LB 8",0,0,0,23,1363967292,new int[]{243,4,166,43,154,4}));
		panel.getLoop2().addDevice(new Device(130,false,"Exit",0,0,0,0,1363947292,new int[]{246,5,166,43,154,4}));

		panelList.add(panel);
		
		
	}

	/**
	 * This is when search button clicked on loading screen
	 * @param view button
	 */
	public void searchPanelsBtn(View view)
	{
		
		searchUDP();
	}

	private void searchUDP(){
		
		//clear current ip list and data list for dialog list view;
		ipListAll.clear();
		dataList.clear();

		if(udpConnection == null ){
			udpConnection = new UDPConnection(UDPConnection.FIND, this);
		}
		else
		{
			udpConnection.closeConnection();
			udpConnection = new UDPConnection(UDPConnection.FIND,this);
			
		}
		
		//send UDP panel search messages
		
		udpConnection.tx("255.255.255.255",UDPConnection.FIND);
		
		progressText.setVisibility(View.INVISIBLE);
		Toast.makeText(this, R.string.toast_search_panel, Toast.LENGTH_LONG).show();	
	}
	
	/**
	 * save checked status to SharedPreference
	 */
	private void saveCheckedStatsToPreference()
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean saveChecked = sp.getBoolean(SettingsActivity.SAVE_CHECKED, true);
		SharedPreferences.Editor editor = sp.edit();
		
		if(saveChecked)
		{
			for(String ip : ipListAll)
			{
				String _ip = new String(ip);
				_ip += " ";

				System.out.println(_ip);
				editor.putBoolean(_ip, ipListSelected.contains(ip));
				editor.commit();
			}
		}
		
		// clear selected IP list 
		//ipSelected.clear();
	}

	/**
	 * Go thought all IP list and put selected ip in the ipSelected set 
	 * @param selected list of position that panels selected in the multi selection dialog
	 */
	private void savePanelSelectionToIpListSelected(List<Integer> selected)
	{
        ipListSelected.clear();
		for(Integer i: selected)
		{
			String item = ipListAll.get(i);
			if(!ipListSelected.contains(item)){
				ipListSelected.add(item);
			}
		}
	}

	

}
