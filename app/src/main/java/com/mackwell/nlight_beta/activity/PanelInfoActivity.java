package com.mackwell.nlight_beta.activity;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mackwell.nlight_beta.R;
import com.mackwell.nlight_beta.models.Panel;

public class PanelInfoActivity extends Activity {
	

    private Panel panel;
    private PanelInfoFragment fragment;

    private final String TAG = "PanelInfoActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panel_info);

        Log.i(TAG,"onCreate");

        panel = getIntent().getParcelableExtra("panel");
        fragment = PanelInfoFragment.newInstance(panel.getIp(),panel.getPanelLocation(),panel);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.panel_detail_container, fragment,"panelFragment");
        //fragmentTransaction.addToBackStack(null);  add fragment to backstack
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();

        getActionBar().setDisplayHomeAsUpEnabled(true);





	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.panel_info, menu);
		return true;
	}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId())
        {

            case android.R.id.home:
                finish();
                return true;

            case R.id.action_show_devices:
                Intent intent = new Intent(this,DeviceActivity.class);
                intent.putExtra("panel",panel);
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
        }



    }

    private void updatePanelInfoFragment(){



        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();


        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();



    }



}
