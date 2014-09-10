package com.mackwell.nlight_beta.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.mackwell.nlight_beta.R;
import com.mackwell.nlight_beta.enums.OverallStatus;

public class PanelListActivity extends ListActivity {
	
	

	private List<Map<String,Object>> dataList = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_list);
        
        dataList = getDataList();
		
		SimpleAdapter adapter = new SimpleAdapter(this,dataList,R.layout.panel_list_row,
				new String[]{"location","ip","img"},
				new int[]{R.id.location,R.id.img});
		setListAdapter(adapter);
		
		
		

		OverallStatus m = OverallStatus.valueOf("OK");
		System.out.println(m.getStatus());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.panel_list, menu);
        return true;
    }
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
    	
    	String ip = (String)dataList.get(position).get("ip");
    	String location = (String)dataList.get(position).get("location");
    	Intent intent = new Intent(this, PanelStatusActivity.class);
		
    	intent.putExtra("ip", ip);
    	intent.putExtra("location",location);
		
		startActivity(intent);
	}
    
    public List<Map<String,Object>> getDataList()
	{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("location","Nigel's Test Demo Unit");
		map.put("ip","192.168.1.17");
		map.put("img", R.drawable.panel);
		
		list.add(map);
		
		map = new HashMap<String,Object>();
		
		map.put("location","Mackwell Factory");
		map.put("ip","192.168.1.21");
		map.put("img", R.drawable.panel);
		
		list.add(map);
		
		/*map = new HashMap<String,Object>();
		
		map.put("location","N-Light CONNECT demo 01");
		map.put("ip","192.168.1.19");
		map.put("img", R.drawable.panel);
		
		list.add(map);*/
		
		map = new HashMap<String,Object>();
		
		map.put("location","Mackwell Link Building");
		map.put("ip","192.168.1.20");
		map.put("img", R.drawable.panel);
		
		list.add(map);
		
		map = new HashMap<String,Object>();
		
		map.put("location","Testing Panel");
		map.put("ip","192.168.1.22");
		map.put("img", R.drawable.panel);
		
		list.add(map);
		
		map = new HashMap<String,Object>();
		
		map.put("location","Mackwell Specials");
		map.put("ip","192.168.1.23");
		map.put("img", R.drawable.panel);
		
		list.add(map);
		
		map = new HashMap<String,Object>();
		
		map.put("location","Technical Demo Board");
		map.put("ip","192.168.1.24");
		map.put("img", R.drawable.panel);
		
		list.add(map);
				
		return list;
	}
    
}
