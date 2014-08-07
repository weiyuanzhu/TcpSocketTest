package com.mackwell.nlight.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mackwell.nlight.R;
import com.mackwell.nlight.nlight.BaseActivity;
import com.mackwell.nlight.nlight.SeekBarDialogFragment;
import com.mackwell.nlight.nlight.SettingsActivity;
import com.mackwell.nlight.nlight.InputDialogFragment.NoticeDialogListener;
import com.mackwell.nlight.socket.TCPConnection;
import com.mackwell.nlight.util.GetCmdEnum;

public class TestActivity extends BaseActivity implements TCPConnection.CallBack,NoticeDialogListener{

	TCPConnection tcpConnection;
	final String ip = "192.168.1.22";
	private ArrayList<Integer> rxData = null;
	
	@Override
	public void receive(List<Integer> rx, String ip){
		
		rxData.addAll(rx);
		System.out.println(rx);
		System.out.println("rxData size: " + rxData.size());
		//tcpConnection.setListening(false);
		//refreshTest();
	}
	
	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		preference();
		
		rxData = new ArrayList<Integer>();
		setContentView(R.layout.activity_test);
		
		tcpConnection = new TCPConnection(this, ip);
	}
	
	

	@Override
	protected void onStart() {
		super.onStart();
		
		
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_devices, menu);
		return true;
	}

	
	
	public void tcpTest(View v)
	{
		/*List<Integer> buffer = new ArrayList<Integer>();
		buffer.add(00);		
		buffer.addAll(DataParser.convertString("test123"));
		System.out.println(buffer);
		List<char[] > commandList = SetCmdEnum.SET_DEVICE_NAME.set(buffer);
		
		commandList = ToggleCmdEnum.REFRESH.toggle(0);
		
		
		
		
		System.out.println("---------------" + Thread.currentThread().toString());
		
		
		for(int i=0; i<5;i++)
		{
			exec.execute(refreshTest);
			
		}*/
			refreshTest();
	}
		
	private void refreshTest(){

			//tcpConnection.closeConnection();
			rxData.clear();
			List<char[] > commandList = GetCmdEnum.GET_REPORT.get();
			//commandList = ToggleCmdEnum.REFRESH.toggle(1);
			
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getTimeInstance();
			System.out.println("---------------" + Thread.currentThread().toString() + "Refreshed Time:  " + sdf.format(cal.getTime()));
			
			tcpConnection.fetchData(commandList);
			

		
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stu
		
		switch (item.getItemId()) {
       
        case R.id.action_settings:
        	Intent settingIntent = new Intent(this, SettingsActivity.class);
        	startActivity(settingIntent);
        	
            return true;
       
    
        default:
            return super.onOptionsItemSelected(item);
    }
	}



	public void messagTest(View v)
	{
		System.out.println(isConnected);
		checkConnectivity();
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		Boolean test = sharedPref.getBoolean("testPref", false);
		System.out.println("testPref--->" + test);

		SeekBarDialogFragment dialog = new SeekBarDialogFragment();
		dialog.show(getFragmentManager(), "SetLocation");
		
	}

	



	@Override
	public void setInformation(String input,int type) {
		System.out.println("callback ------> " + input);
		
	}


	private void preference()
	{
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

	}

	
	
}
