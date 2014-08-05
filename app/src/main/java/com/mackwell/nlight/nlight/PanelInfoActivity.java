package com.mackwell.nlight.nlight;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.mackwell.nlight.R;
import com.mackwell.nlight.models.Panel;
import com.mackwell.nlight.socket.TCPConnection;
import com.mackwell.nlight.util.CommandFactory;
import com.mackwell.nlight.util.DataParser;

public class PanelInfoActivity extends ListActivity  implements TCPConnection.CallBack{
	
	private Handler progressHandler;
	private Handler listUpdateHandler;

	private ProgressBar progressBar = null;
	
	private List<Map<String,Object>>  listDataSource; //data for listview
	
	private List<Integer> rxBuffer = null;  //raw data pull from panel
	
	private List<List<Integer>> panelData = null;  //all panel data (removed junk bytes)
	private List<List<Integer>> eepRom = null;		//panel eeprom data (bytes)
	private List<List<List<Integer>>> deviceList = null;	//device list (bytes)
	
	private TCPConnection connection;	
	
	private Panel panel = null;
	
	private SimpleAdapter simpleAdapter;
	
	private List<char[]> commandList;
	
	private int packageCount;
	


	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panel_info);
		
		
		Intent intent = getIntent();
		
		ArrayList<Panel> panelList = intent.getParcelableArrayListExtra("panelList");
		
	
		
		panel = panelList.get(4);
		
		progressHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				
				progressBar.setProgress(msg.arg1);
				
				if(msg.arg1 == 16)
				{
					progressBar.setProgress(0);
					progressBar.setVisibility(View.INVISIBLE);
					
				}
				
			}
			
			
		};
		
		listUpdateHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				
				try {
					panel = new Panel (eepRom,deviceList,null);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				listDataSource = getData(panel);
				
				simpleAdapter = new SimpleAdapter(PanelInfoActivity.this, listDataSource, R.layout.panel_info_row, 
						new String[] {"text1","text2"}, new int[] {R.id.textView1,R.id.textView2});
				
				setListAdapter(simpleAdapter);
			
			}
			
			
		};
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		
		progressBar.setMax(16);
		
		
		
		//setup list view
		
		simpleAdapter = new SimpleAdapter(this, getData(panel), R.layout.panel_info_row, new String[] {"text1","text2"}, new int[] {R.id.panel_description,R.id.panel_value});
		
		setListAdapter(simpleAdapter);
		
	}
	
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		
		if (newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE) {
	           // Nothing need to be done here
	            
	        } else {
	           // Nothing need to be done here
	        }
		
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.panel_info, menu);
		return true;
	}

	
	//function for delegate interface
	@Override
	public void receive(List<Integer> rx,String ip) {
		
		packageCount += 1 ;
		
		Message msg = progressHandler.obtainMessage();
		msg.arg1 = packageCount;
		
		progressHandler.sendMessage(msg);
		
		
		rxBuffer.addAll(rx);

		connection.setListening(true);
				
		/*if(this.rxBuffer.size() > 15000)
		{		
			connection.setIsClosed(true);		
		}*/
		
		
		if(packageCount == 16)
		{
			connection.closeConnection();
			//progressBar.setVisibility(View.INVISIBLE);
			
			parse();
			
			
		}
		System.out.println("Actual bytes received: " + rxBuffer.size());
		
		
		
	}
	
	public void parse()
	{
		
		panelData = DataParser.removeJunkBytes(rxBuffer);
		eepRom = DataParser.getEepRom(panelData);
		
		System.out.println("================EEPROM========================");
		
		for(int i=0; i<eepRom.size();i++)
		{
			System.out.println("eepRom "+ i + ": \n" + eepRom.get(i));
		}
		
		System.out.println("================Device List========================");
		deviceList = DataParser.getDeviceList(panelData,eepRom);
		
		
		for(int i=0; i<deviceList.size();i++)
		{
			System.out.println("Loop: " + i);
			for(int j =0; j<deviceList.get(i).size();j++)
			{
				System.out.println("device: "+ j + " " + deviceList.get(i).get(j));
			}
		}

		Message msg = listUpdateHandler.obtainMessage();
		listUpdateHandler.sendMessage(msg);
		
	}

	public void fetchPanelInfo(View v)
	{
		packageCount = 0;
		progressBar.setVisibility(View.VISIBLE);
		
		rxBuffer = new ArrayList<Integer>();
		commandList = new ArrayList<char[]>();
		
		//char[] getPackage0 = new char[] {2, 165, 64, 0, 32, 0,0x5A,0xA5,0x0D,0x0A};
		//char[] getPackage1 = new char[] {2, 165, 64, 15, 96, 0,0x5A,0xA5,0x0D,0x0A};
		//char[] getPackage2 = new char[] {2, 165, 64, 15, 96, 0,0x5A,0xA5,0x0D,0x0A};
		//char[] getConfig = new char[] {0x02,0xA0,0x21,0x68,0x18,0x5A,0xA5,0x0D,0x0A};
		
		commandList = CommandFactory.getPanelInfo();
		//commandList.add(getPackage1);
		
		connection = new TCPConnection(this, "192.168.1.24");
		connection.fetchData(commandList);
		
	}
	

	public List<Map<String,Object>> getData(Panel panel)
	{
		
		listDataSource = new ArrayList<Map<String,Object>>();
		
			
		Map<String,Object> map = new HashMap<String,Object>();
			
		map.put("text1", "Location");
		map.put("text2", panel==null? "n/a" : panel.getPanelLocation());
		
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		
		map.put("text1", "SerialNumber:");
		map.put("text2", panel==null? "n/a" : panel.getSerialNumber());
			
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("text1", "GTIN:");
		map.put("text2", panel==null? "n/a" : panel.getGtin());
			
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("text1", "Contact");
		map.put("text2", panel==null? "n/a" : panel.getContact());
		
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("text1", "Tel:");
		map.put("text2", panel==null? "n/a" : panel.getTel());
			
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("text1", "Mobile:");
		map.put("text2", panel==null? "n/a" : panel.getMobile());
			
		listDataSource.add(map);
	
		map = new HashMap<String,Object>();
		
		map.put("text1", "FirmWare Version:");
		map.put("text2", panel==null? "n/a" : panel.getVersion());
			
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		map.put("text1", "Report Usage:");
		map.put("text2", panel==null? "n/a" : panel.getReportUsage());
			
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		map.put("text1", "Passcode:");
		map.put("text2", panel==null? "n/a" : panel.getPasscode());
			
		listDataSource.add(map);
	
		return listDataSource;
	}
	
	
	//button --> device list
	public void showDeviceList(View v){
		
		Intent intent = new Intent(this, DeviceListActivity.class);

		if(panel!=null){
			
			intent.putExtra("loop1",panel.getLoop1());
			intent.putExtra("loop2",panel.getLoop2());
			
		}
		
		startActivity(intent);
		
	}





	@Override
	public void error(String ip) {
		// TODO Auto-generated method stub
		
	}
}
