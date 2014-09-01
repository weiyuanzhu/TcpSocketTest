package com.mackwell.nlight.nlight;

import android.app.FragmentTransaction;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mackwell.nlight.R;
import com.mackwell.nlight.models.Report;
import com.mackwell.nlight.socket.TCPConnection;
import com.mackwell.nlight.util.Constants;
import com.mackwell.nlight.util.DataParser;
import com.mackwell.nlight.util.GetCmdEnum;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends BaseActivity {

    private static final String TAG = "ReportActivity";
    private static final String TAG_RECEIVE = "ReportActivity_Receive";

    private String ip;
    private List<Integer> reportRawData;
    private List<Report> reportList;
    private Handler mHandler;
    private ReportFragment fragment;


    @Override
    public void receive(List<Integer> rx, String ip) {
        Log.d(TAG_RECEIVE,ip);
       System.out.println(rx);
        if (rx.get(1) == Constants.MASTER_GET && rx.get(2) == Constants.GET_REPORT) {
            reportRawData.addAll(rx.subList(3, rx.size() - 6));
        } else {
            if (rx.get(1) == Constants.FINISH) {
                Log.i(TAG,Integer.toString(reportRawData.size()));
                System.out.println(reportRawData);
                reportList = DataParser.getReportList(reportRawData);
                mHandler.post(test);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        ip = getIntent().getStringExtra("ip");
        isDemo = getIntent().getBooleanExtra("demo",true);
        reportRawData = new ArrayList<Integer>();
        reportList = new ArrayList<Report>();

        fragment = ReportFragment.newInstance("arg1","arg2");

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.report_container,fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if(isConnected && !isDemo) connection = new TCPConnection(this,ip);

        mHandler = new Handler();



    }

    @Override
    protected void onStart() {
        super.onStart();
        if(connection == null){
            connection = new TCPConnection(this,ip);

        }
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
        reportRawData.clear();



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

    Runnable test = new Runnable() {
        @Override
        public void run() {
            fragment.updateList(reportList);
        }
    };
}
