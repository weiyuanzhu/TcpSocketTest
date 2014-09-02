package com.mackwell.nlight.nlight;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.mackwell.nlight.R;
import com.mackwell.nlight.messageType.FailureStatus;
import com.mackwell.nlight.messageType.FailureStatusFlag;
import com.mackwell.nlight.models.Report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportFaultsActivity extends BaseActivity {

    private static final String TAG = "ReportFaultsActivity";


    private Report report;
    private ListView mListView;
    private SimpleAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_faults);

        report = (Report) getIntent().getSerializableExtra("report");

        mListView = (ListView) findViewById(R.id.report_faults_listView);
        mAdapter = new SimpleAdapter(this,getDataList(),R.layout.report_faults_row,
                new String[] {"loop","device","serial","location","description"},
                new int[] {R.id.report_fault_loop_textView,R.id.report_fault_device_textView,R.id.report_fault_serial_textView,R.id.report_fault_location_textView,R.id.report_fault_description_textView});
        mListView.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report_faults, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Map<String,String>> getDataList(){

        ArrayList<Map<String,String>> dataList = new ArrayList<Map<String, String>>();
        HashMap<String,String> map;

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");


        map = new HashMap<String, String>();
        map.put("loop", "Loop");
        map.put("device", "Device/Group");
        map.put("serial", "Serial number");
        map.put("location","Location");
        map.put("description","Fault description");
        dataList.add(map);

        if (report !=null) {

            for(int i=0; i<report.getFaultyDeviceList().size();i++) {
                map = new HashMap<String, String>();

                ArrayList<Integer> list = (ArrayList<Integer>) report.getFaultyDeviceList().get(i);

                int address = list.get(0);
                int fs = list.get(1);
                long serialNumber = list.get(2) + 256 * list.get(3) + 65536 * list.get(4) + 16777216L * list.get(5);


                map.put("loop", (address & 0x80)==0? "Loop1" : "Loop2" );
                map.put("device", Integer.toString(address>192? (address-192) : (address-64)));
                map.put("serial", Long.toString(serialNumber));
                map.put("location", "Location");
                map.put("description", getFailureStatusText(fs));
                dataList.add(map);

            }
        }

        return dataList;
    }

    public String getFailureStatusText(int failureStatus) {
        StringBuilder sb = new StringBuilder();

        if (failureStatus ==0) {
            return "Device lost";
        }

        EnumSet<FailureStatus> fsSet = new FailureStatusFlag().getFlagStatus(failureStatus);


            if (fsSet.size()==0)
            {
                sb.append("All OK");
            }
            else{
                for(FailureStatus fs : fsSet)
                {
                    sb.append(fs.getDescription()+" , ");
                }
                System.out.println(sb);

                //trim last ","
                sb.deleteCharAt(sb.length()-2);

            }


        return sb.toString();

    }

}
