package com.mackwell.nlight_beta.activity;

import java.util.List;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mackwell.nlight_beta.R;
import com.mackwell.nlight_beta.models.Device;
import com.mackwell.nlight_beta.socket.TcpLongConnection;


public class DeviceInfoActivity extends Activity implements TcpLongConnection.CallBack{
	
	private static final String TAG = "DeviceInfoActivity";

	private TcpLongConnection connection;

	private Device device;
    private DeviceInfoFragment fragment;

    private ImageView logoImageView = null;
    private TextView faultyNoTextView = null;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_info);


        Log.i(TAG, "onCreate");

        device = getIntent().getParcelableExtra("device");
        fragment = DeviceInfoFragment.newInstance(device, false);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.device_detail_container, fragment,"deviceFragment");
        //fragmentTransaction.addToBackStack(null);  add fragment to backstack
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();

        getActionBar().setDisplayHomeAsUpEnabled(true);
		
        logoImageView = (ImageView) findViewById(R.id.deviceInfo_image);
        faultyNoTextView = (TextView) findViewById(R.id.deviceInfo_faultyNo_text);

        logoImageView.setVisibility(View.INVISIBLE);
        faultyNoTextView.setVisibility(View.INVISIBLE);

		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_info, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
	public void receive(List<Integer> rx, String ip) {
		System.out.println(rx);

	}

	@Override
	public void onError(String ip,Exception e) {
		// TODO Auto-generated method stub
		
	}



}
