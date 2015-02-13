package com.mackwell.nlight_beta.activity;

import com.mackwell.nlight_beta.models.Panel;
import com.mackwell.nlight_beta.socket.UDPConnection;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.mackwell.nlight_beta.R;
import com.mackwell.nlight_beta.util.MySQLiteController;
import com.mackwell.nlight_beta.util.MySQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CachedPanelListActivity extends Activity  implements UDPConnection.UDPCallback{

    private static final String TAG = "CachedPanelListActivity";
    private static final String PATTERN =
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";



    //activity controls
    private ListView mListView;
    private EditText ipEditText;
    private Button searchBtn;

    //list view adapter and data
    private SimpleCursorAdapter mAdapter;
    private Cursor mCursor;
    private List<Panel> panelList;
    private MySQLiteController sqlController;

    private String[] from;
    private int[] to;

    //connection
    private UDPConnection udpConnection;
    private String searchIP;

    private Handler mHandler;

    //implements interfaces
    @Override
    public int addIp(byte[] mac,String ip) {

        String macString = String.format("%02X:%02X:%02X:%02X:%02X:%02X",
                mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);

        Log.d(TAG,"UDP received MAC: " + macString);
        Log.d(TAG,"UDP received IP: " + ip);

        mHandler.post(new UpdateListView(macString,ip));



        return 0;
    }

    //acitivity life circles
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cached_panel_list);

        mHandler = new Handler();

        //setup list view
        mListView = (ListView) findViewById(R.id.cached_panel_list_listView);
        ipEditText = (EditText) findViewById(R.id.cached_panel_list_ip_editText);
        searchBtn = (Button) findViewById(R.id.cached_panel_list_search_button);


        //ip address filter
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       android.text.Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                    if (!resultingTxt.matches ("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i=0; i<splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }

        };
        ipEditText.setFilters(filters);

        sqlController = new MySQLiteController(this);
        panelList = new ArrayList<Panel>();

        sqlController.open();
        mCursor = sqlController.readData();


        from = new String[]{
                MySQLiteOpenHelper.COLUMN_PANELIP,
                MySQLiteOpenHelper.COLUMN_PANELMAC,
                MySQLiteOpenHelper.COLUMN_PANELLOCATION};


        to = new int[]{R.id.cached_panel_list_ip_textView, R.id.cached_panel_list_macAddress_textView, R.id.cached_panel_list_location_textView};

        mAdapter = new SimpleCursorAdapter(this, R.layout.panel_list_row3, mCursor, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


        mListView.setAdapter(mAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mListView.setItemChecked(position, true);
                mListView.setSelection(position);
            }
        });

        sqlController.close();

        //set <Back
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(udpConnection!=null)
        {
            udpConnection.closeConnection();
            udpConnection = null;
        }
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

    public void remove(View view)
    {
        Log.d(TAG, "IP entered: " + ipEditText.getText().toString());
        int position = mListView.getCheckedItemPosition();

        //check if there is any row selected
        if(position!=-1) {
            mCursor.moveToPosition(position);
            String ip = mCursor.getString(mCursor.getColumnIndex(MySQLiteOpenHelper.COLUMN_PANELIP));

            sqlController.open();

            sqlController.deletePanel(ip);
            mCursor = sqlController.readData();
            mAdapter.changeCursor(mCursor);
            mAdapter.notifyDataSetChanged();

            sqlController.close();
        }
        else{
            Toast.makeText(this,R.string.toast_select_panel,Toast.LENGTH_SHORT).show();
        }
    }

    public void search(View view)
    {
        searchIP = ipEditText.getText().toString();
        if(validate(searchIP)) {

            searchBtn.setEnabled(false);
            Log.d(TAG, "IP entered: " + searchIP);
            sqlController.open();

            //check if ip is already in the list

            if (sqlController.findPanelByIp(searchIP) != null) {
                searchBtn.setEnabled(true);
                Toast.makeText(this, R.string.toast_panel_exist, Toast.LENGTH_SHORT).show();

            } else {
                if (udpConnection == null) {
                    udpConnection = new UDPConnection(UDPConnection.FIND, this);
                }

                udpConnection.tx(searchIP, UDPConnection.FIND);
                Toast.makeText(this, R.string.toast_search_signle_ip, Toast.LENGTH_SHORT).show();

            }

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sqlController.open();

                    //check if ip is already in the list
                    if (sqlController.findPanelByIp(searchIP) == null) {
                        searchBtn.setEnabled(true);
                        Toast.makeText(CachedPanelListActivity.this, searchIP + R.string.toast_panel_not_found, Toast.LENGTH_SHORT).show();
                    }
                }
            }, 3000);
        }
        else{
            Toast.makeText(this,R.string.toast_invalid_ip,Toast.LENGTH_SHORT).show();
        }

        //hide input keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ipEditText.getWindowToken(), 0);

    }

    class UpdateListView implements Runnable{

        private String mac;
        private String ip;

        public UpdateListView(String mac, String ip){
            this.mac = mac;
            this.ip = ip;
        }

        @Override
        public void run() {
            Panel panel = new Panel();
            panel.setIp(ip);
            panel.setMacString(mac);
            panel.setPanelLocation("");
            panelList.add(panel);

            sqlController.open();

            sqlController.insertPanel(panel);
            mCursor = sqlController.readData();
            mAdapter.changeCursor(mCursor);
            mAdapter.notifyDataSetChanged();

            sqlController.close();

            searchBtn.setEnabled(true);
            Toast.makeText(CachedPanelListActivity.this, R.string.toast_found_panel, Toast.LENGTH_SHORT).show();

        }
    }

    public static boolean validate(final String ip){

        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

}
