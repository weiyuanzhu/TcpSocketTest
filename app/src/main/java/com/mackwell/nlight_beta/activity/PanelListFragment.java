package com.mackwell.nlight_beta.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.mackwell.nlight_beta.R;
import com.mackwell.nlight_beta.models.Panel;
import com.mackwell.nlight_beta.socket.TcpLongConnection;


/**
 * A simple   {@link android.support.v4.app.Fragment}  subclass. Activities thatcontain this fragment must implement the  {@link PanelListFragment.OnPanelListItemClickedCallBack}  interface to handleinteraction events.
 */
public class PanelListFragment extends ListFragment implements TcpLongConnection.CallBack{
	
	/**
	 * This interface must be implemented by activities that contain this fragment to allow an interaction in this fragment to be communicated to the activity and potentially other fragments contained in that activity. <p> See the Android Training lesson <a href= "http://developer.android.com/training/basics/fragments/communicating.html" >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnPanelListItemClickedCallBack {
		// TODO: Update argument type and name
		public void onListItemClicked(String ip, String location,int index);
		public void getAllPanels();
		public void passTest();

		
	}
	
	private List<Panel> panelList;
	
	

	private Handler statusUpdateHandler;
	
	//private Button refreshBtn;
	private Button getAllPanelsBtn;
	private Button passTest;

	private OnPanelListItemClickedCallBack mListener;
	
	private List<Map<String,Object>> dataList = null;
	private SimpleAdapter simpleAdapter;
	private int mCurCheckPosition = -1;
	
	//private List<TCPConnection> connectionList;
	private List<char[]> commandList;
	
	private boolean isDemo; 
	private boolean isConnected;
	

	/* (non-Javadoc) implementing TCPConnection callback for retrieve data from another thread
	 * @see nlight_android.socket.TCPConnection.CallBack#receive(java.util.List, java.lang.String)
	 */
	@Override
	public void receive(List<Integer> rx,String ip) {
		System.out.println(rx);
		
		/*Message msg = statusUpdateHandler.obtainMessage();
		msg.arg1 = rx.get(3);
		msg.obj = ip;

		statusUpdateHandler.sendMessage(msg);
		
		for(TCPConnection c: connectionList){
			
			if(c.getIp().equals(ip))
			{
				c.setListening(true);
				
			}
			
		}*/
		
	}
	
	
	public PanelListFragment() {
		// Required empty public constructor
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_panel_list, container, false);
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnPanelListItemClickedCallBack) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
		
		this.statusUpdateHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				
				String ip = (String) msg.obj;
				for(Map<String,Object> map:dataList)		
				{
					if(map.get("ip").equals(ip))
					{
						if(msg.arg1 == 1) {
							map.put("img", R.drawable.greentick);
							
						}
						else if(msg.arg1 == 3) {
							map.put("img", R.drawable.redcross);
						}
						
					}
				}
				
			
				
				simpleAdapter.notifyDataSetChanged();
				
			}
			
			
		};
	}

	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
	
		//refreshBtn = (Button) getActivity().findViewById(R.id.panelList_refreshButton);
		//refreshBtn.setOnClickListener(refreshClicked);
		passTest = (Button) getActivity().findViewById(R.id.panelList_passTest);
		
		getAllPanelsBtn = (Button) getActivity().findViewById(R.id.panelList_getAllPanelButton);
		getAllPanelsBtn.setOnClickListener(getAllPanelsListener);
		passTest.setOnClickListener(passListener);
		dataList = getDataList();
		
		
		//ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_activated_1,dataList2);
		
		//setListAdapter (myAdapter);
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		simpleAdapter = new SimpleAdapter(getActivity(),dataList,R.layout.panel_list_row,
				new String[]{"location","img"},
				new int[]{R.id.location,R.id.img});
		setListAdapter(simpleAdapter);

        //restore fragment state from saved state
        if (savedInstanceState!=null) {
            int position = savedInstanceState.getInt("position");
            mCurCheckPosition = position;
            getListView().setItemChecked(position,true);
            if (position!=-1) {
                mListener.onListItemClicked(panelList.get(position).getIp(), panelList.get(position).getPanelLocation(), position);
            }

        }
        //create TCPConnection for each panel and open rx threads for listening
		//if it is in live mode
		/*if(!isDemo && isConnected){
			
			connectionList = new ArrayList<TCPConnection>();
			
			//find this fragment itself so it can be passed to TCPConnection constructor as a Callback
			PanelListFragment currentFragment= (PanelListFragment)getFragmentManager().findFragmentByTag("panelListFragment");
			
			for(int i=0; i<dataList.size(); i++)
			{
				commandList = CommandFactory.getOverallStatus();
				String ip = (String) panelList.get(i).getIp();
				
				TCPConnection connection = new TCPConnection(currentFragment,ip);
				connectionList.add(connection);
			
			}
		}*/
		
		
		
	}



	@Override
	public void onPause() {
		System.out.println("------------PanelListFragment onPause---------");
		
		/*if(connectionList!=null){
			for(TCPConnection tcp: connectionList){
			
				if(tcp!=null){
					tcp.setListening(false);
				}
			}
		}*/
		
		super.onPause();
	}


	@Override
	public void onStop() {
		System.out.println("------------PanelListFragment onPause---------");
		super.onStop();
	}


	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", mCurCheckPosition);
    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		mCurCheckPosition = position;
		
		String ip = (String)panelList.get(position).getIp();
    	String location = (String)dataList.get(position).get("location");
		//System.out.println(position);
		
		getListView().setItemChecked(position, true);
		
		mListener.onListItemClicked(ip, location, position);
		
	}
	

	public void refreshStatus(boolean isDemo, boolean isConnected) {
			
			//check connectivity and demo mode flag
			
		/*if(!isDemo && isConnected){
			
			
			
			for(TCPConnection tcp : connectionList )
			{
				commandList = CommandFactory.getOverallStatus();
				tcp.fetchData(commandList);
			}
		}*/
	}
		

	
	
	
	private List<Map<String,Object>> getDataList()
	{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		Map<String, Object> map  = null;
		
		for(int i =0; i<panelList.size();i++)
		{
			Panel p = panelList.get(i);
			
			map = new HashMap<String, Object>();
			map.put("location",p.getPanelLocation().trim());
			map.put("ip",p.getIp());
			if(p.getOverAllStatus()== Panel.OK){
				map.put("img", R.drawable.greentick);
			}else map.put("img", R.drawable.redcross);
			map.put("faultyNo", "("+p.getFaultDeviceNo()+")");
			list.add(map);
	
		}
	
		return list;
	}

	
	
	


	OnClickListener getAllPanelsListener = new OnClickListener()
	{

		@Override
		public void onClick(View arg0) {
			mListener.getAllPanels();
			
		}
		
		
	};
	
	OnClickListener passListener = new OnClickListener()
	{
		@Override
		public void onClick(View arg0) {
			mListener.passTest();
			
		}
		
		
	};
	
	protected List<Panel> getPanelList() {
		return panelList;
	}

	protected void setPanelList(List<Panel> panelList) {
		this.panelList = panelList;
	}

	@Override
	public void onError(String ip,Exception e) {
		System.out.println("=============PanelListFragment Connection ERROR================");
		
	}



	/**
	 * @return the isDemo
	 */
	public boolean isDemo() {
		return isDemo;
	}



	/**
	 * @param isDemo the isDemo to set
	 */
	public void setDemo(boolean isDemo) {
		this.isDemo = isDemo;
	}



	/**
	 * @return the isConnected
	 */
	public boolean isConnected() {
		return isConnected;
	}



	/**
	 * @param isConnected the isConnected to set
	 */
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	
	public void updateList(int position, String location){
		
		dataList.get(position).put("location", location);
		simpleAdapter.notifyDataSetChanged();
		
		
	}
	
	public void clearSelection()
	{
		getListView().clearChoices();
	}

    @Override
    public void setSelection(int position) {
        super.setSelection(position);

        getListView().setItemChecked(position, true);
    }
}
