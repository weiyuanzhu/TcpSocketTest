package com.mackwell.nlight.nlight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mackwell.nlight.R;
import com.mackwell.nlight.models.Device;
import com.mackwell.nlight.socket.TCPConnection;
import com.mackwell.nlight.util.CommandFactory;
import com.mackwell.nlight.util.ToggleCmdEnum;

public class DeviceInfoActivity extends ListActivity implements TCPConnection.CallBack{
	
	
	private TCPConnection connection;
	private TextView title;
	
	private SimpleAdapter simpleAdapter;
	
	private List<Map<String,Object>> listDataSource;
	
	private Device device;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_info);
		
		title = (TextView) findViewById(R.id.devicetitle);
		
		
		String str = getIntent().getStringExtra("deviceName");
		
		device = getIntent().getParcelableExtra("device");
		
		simpleAdapter = new SimpleAdapter(this, getData(device), R.layout.device_info_row, 
				new String[] {"text1","text2"}, new int[] {R.id.deviceDescription,R.id.deviceValue});
		
		
		title.setText(str);
		
		setListAdapter(simpleAdapter);
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_info, menu);
		return true;
	}
	
	public List<Map<String,Object>> getData(Device device)
	{
		
		listDataSource = new ArrayList<Map<String,Object>>();
		
			
		Map<String,Object> map = new HashMap<String,Object>();
			
		map.put("text1", "Address");
		map.put("text2", device==null? "n/a" : device.getAddress());
		
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		
		map.put("text1", "SerialNumber:");
		map.put("text2", device==null? "n/a" : device.getSerialNumber());
			
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("text1", "GTIN:");
		map.put("text2", device==null? "n/a" : "-");
			
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("text1", "Location");
		map.put("text2", device==null? "n/a" : "-");
		
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("text1", "Emergency mode");
		map.put("text2", device==null? "n/a" : device.getEmergencyMode());
			
		listDataSource.add(map);
		map = new HashMap<String,Object>();
		
		map.put("text1", "Emergency Status");
		map.put("text2", device==null? "n/a" : device.getEmergencyStatus());
			
		listDataSource.add(map);
	
		map = new HashMap<String,Object>();
		
		map.put("text1", "Failure Status");
		map.put("text2", device==null? "n/a" : device.getFailureStatus());
			
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		map.put("text1", "Battery Level");
		map.put("text2", device==null? "n/a" : device.getBattery());
			
		listDataSource.add(map);
		
		map = new HashMap<String,Object>();
		map.put("text1", "Communication Status");
		map.put("text2", device==null? "n/a" : device.isCommunicationStatus());
			
		listDataSource.add(map);
	
		return listDataSource;
	}
	
	public void ftTest(View v)
	{
		System.out.println("----------ftTest--------");
		 //commandList = CommandFactory.ftTest(device.getAddress());
		 List<char[] > commandList = ToggleCmdEnum.FT.toggle(64);
		
		TCPConnection connection = new TCPConnection (this,"192.168.1.24");
		connection.fetchData(commandList);
		
	}
	
	public void stopTest(View v)
	{
		System.out.println("----------ftTest--------");
		List<char[] > commandList = CommandFactory.stopTest(device.getAddress());
		
		TCPConnection connection = new TCPConnection (this, "192.168.1.24");
		connection.fetchData(commandList);
		
	}
	
	

	@Override
	public void receive(List<Integer> rx, String ip) {
		System.out.println(rx);
		connection.setListening(true);
		
	}

	@Override
	public void error(String ip) {
		// TODO Auto-generated method stub
		
	}



}
