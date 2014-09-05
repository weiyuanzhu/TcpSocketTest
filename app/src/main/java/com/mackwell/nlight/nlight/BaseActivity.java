package com.mackwell.nlight.nlight;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;


import com.mackwell.nlight.R;
import com.mackwell.nlight.socket.*;
import com.mackwell.nlight.socket.TCPConnection.CallBack;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.view.Menu;

/**
 * @author  weiyuan zhu base class for all other activities  to check device connecctivities
 */

public class BaseActivity extends Activity implements CallBack{
	
	protected static boolean isDemo = false;
	
	//protected flags for connections 
	
	// Whether device is connected
	protected static boolean isConnected = false;
	protected static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    protected static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;

	@SuppressWarnings("unused")
	protected TCPConnection connection;

    //Android app shared preference object
    protected SharedPreferences sharedPreferences = null;
    protected Drawable appImage = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);



	}


    @Override
    protected void onResume() {
        super.onResume();

//      update activity action bar icon, when actionbar exist
        String imageLocation = sharedPreferences.getString("pref_app_icons","default image");
        Uri uri = Uri.parse(imageLocation);



        if(!imageLocation.equals("default image")) try {
            InputStream stream = getContentResolver().openInputStream(uri);
            appImage = Drawable.createFromStream(stream, "test");
            if (getActionBar()!=null) {
                getActionBar().setIcon(appImage);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(connection!=null){
            connection.closeConnection();
            connection = null;
        }


    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.base, menu);
		return true;
	}
	

	

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
	        case R.id.action_settings:
	                Intent settingsActivity = new Intent(getBaseContext(), com.example.nclient.SettingsActivity.class);
	                startActivity(settingsActivity);
	                return true;

	        default:
	                return super.onOptionsItemSelected(item);
	        }
	}*/

	/**
	 * method 
	 */
	protected void checkConnectivity() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
        	isConnected = true;
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            System.out.println("wifi----------->" + wifiConnected );
            System.out.println("3G----------->" + mobileConnected);
        } else {
            wifiConnected = false;
            mobileConnected = false;
            isConnected = false;
            System.out.println("not connected");
        }

    }

	@Override
	public void receive(List<Integer> rx, String ip) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String ip) {
		// TODO Auto-generated method stub
		
	}

	


}
