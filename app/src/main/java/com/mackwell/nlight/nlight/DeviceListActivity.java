package com.mackwell.nlight.nlight;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.mackwell.nlight.R;
import com.mackwell.nlight.models.Loop;

public class DeviceListActivity extends ListActivity {
	
	private Loop loop1;
	private Loop loop2;
	private String title;
	
	
	//private List<Loop> loops;
	
	private String[] deviceList;
	private int deviceNumber;
	
	private ArrayAdapter<String> arrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_list);
		
		
		
		Intent intent = getIntent();
		
		title = intent.getStringExtra("location");
		setTitle(title);
		
		loop1 = (Loop) intent.getParcelableExtra("loop1");  
		loop2 = (Loop) intent.getParcelableExtra("loop2");
		
		System.out.println("loop1 n:" + (loop1 == null? " 0" :loop1.getDeviceNumber()));
		System.out.println("loop2 n:" + (loop2 == null? " 0" :loop2.getDeviceNumber()));
		
		deviceNumber = (loop1 ==null? 0:loop1.getDeviceNumber()) + (loop2 ==null? 0:loop2.getDeviceNumber());
		
		deviceList = new String[deviceNumber];
		
		for (int i =0; i<deviceList.length;i++)
		{
			deviceList[i] = new String("Device " + i);
			
		}
		
		arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2,android.R.id.text1, deviceList);
		
		setListAdapter(arrayAdapter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_list, menu);
		
		//get menu item for Search action button 
		//MenuItem searchItem = menu.findItem(R.id.action_search_device);
		
		//get SearchView
		//SearchView searchView = (SearchView) searchItem.getActionView();

		
		
		return true;
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		//System.out.println(deviceList[position]);
		
		Intent intent = new Intent(this, DeviceInfoActivity.class);
		
		intent.putExtra("deviceName", deviceList[position]);
		
		if(position < 3 )
		{
			intent.putExtra("device", loop1.getDevice(position));
		}
		else
		{
			intent.putExtra("device", loop2.getDevice(position-3));
		}
			
		
		startActivity(intent);
	}
	
	

}
