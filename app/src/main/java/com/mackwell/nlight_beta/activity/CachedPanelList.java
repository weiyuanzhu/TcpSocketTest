package com.mackwell.nlight_beta.activity;

import com.mackwell.nlight_beta.socket.UDPConnection;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.mackwell.nlight_beta.R;
import com.mackwell.nlight_beta.adapter.CachedPanelListAdapter;
import com.mackwell.nlight_beta.socket.UDPConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CachedPanelList extends Activity  implements UDPConnection.UDPCallback{

    private static final String TAG = "CachedPanelListActivity";

    //activity controls
    private ListView mListView;
    private EditText ipEditText;
    private CachedPanelListAdapter mAdapter;

    //connection
    private UDPConnection udpConnection;
    private String searchIP;

    //implements interfaces
    @Override
    public int addIp(byte[] mac,String ip) {

        String macString = String.format("%02X:%02X:%02X:%02X:%02X:%02X",
                mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);

        Log.d(TAG,"UDP received MAC: " + macString);


        Log.d(TAG,"\nUDP received IP: " + ip);
        return 0;
    }

    //acitivity life circles
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cached_panel_list);

        //setup list view
        mListView = (ListView) findViewById(R.id.cached_panel_list_listView);
        ipEditText = (EditText) findViewById(R.id.cached_panel_list_ip_editText);

        String[] days = new String[]{"Monday","Tuesday"};
        mAdapter = new CachedPanelListAdapter(this, getDataList());

        mListView.setAdapter(mAdapter);

        //set <Back
        if(getActionBar()!=null)
        {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    private List<Map<String,String>> getDataList(){
        List<Map<String,String>> dataList = new ArrayList<Map<String, String>>();

        Map map = new HashMap<String,String>();
        map.put("ip","IP Address");
        map.put("mac","Mac Address");
        map.put("location","Panel Location");

        dataList.add(map);

        map = new HashMap<String,String>();
        map.put("ip","192.168.1.17");
        map.put("mac","00:92:12:8e:12:09");
        map.put("location","test location");

        dataList.add(map);

        map = new HashMap<String,String>();
        map.put("ip","192.168.1.19");
        map.put("mac","01:92:12:8f:12:09");
        map.put("location","Link building");

        dataList.add(map);

        return dataList;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cached_panel_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void search(View view)
    {
        searchIP = ipEditText.getText().toString();
        Log.d(TAG,"IP entered: " + searchIP);
        if (udpConnection == null) {
            udpConnection = new UDPConnection(UDPConnection.FIND,this);
            udpConnection.tx(searchIP,UDPConnection.FIND);
        }

    }

    public void remove(View view)
    {
        Log.d(TAG,"IP entered: " + ipEditText.getText().toString());

    }

}
