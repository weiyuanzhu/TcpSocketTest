package com.mackwell.nlight.nlight;

import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mackwell.nlight.R;
import com.mackwell.nlight.socket.TCPConnection;
import com.mackwell.nlight.util.GetCmdEnum;

import java.util.List;

public class ReportActivity extends BaseActivity {

    private static final String TAG = "ReportActivity";

    private String ip;
    private boolean demo;


    @Override
    public void receive(List<Integer> rx, String ip) {
        Log.d(TAG,ip);
       System.out.println(rx);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        ip = getIntent().getStringExtra("ip");

        ReportFragment fragment = ReportFragment.newInstance("arg1","arg2");

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.report_container,fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();

        getActionBar().setDisplayHomeAsUpEnabled(true);


        if(isConnected && !isDemo) connection = new TCPConnection(this,ip);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_fetch_report:
                Log.i(TAG,"fetch_report");
                fetchReport();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void fetchReport()
    {
        Log.i(TAG,ip);
        if (isConnected && !isDemo) {
//            connection = new TCPConnection(this,ip);
            connection.fetchData(GetCmdEnum.GET_REPORT.get());
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_report, container, false);
            return rootView;
        }
    }
}
