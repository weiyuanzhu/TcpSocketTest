package com.mackwell.nlight_beta.activity;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.mackwell.nlight_beta.R;
import com.mackwell.nlight_beta.adapter.DeviceInfoListAdapter;
import com.mackwell.nlight_beta.models.Device;

import android.app.Activity;
import android.app.ListFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link DeviceInfoFragment.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link DeviceInfoFragment#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
/**
 * @author weiyuan zhu
 *
 */
public class DeviceInfoFragment extends ListFragment {


	public TextView updateStampTextView;
	
	private Calendar cal;
	
	private static final String ARG_DEVICE = "device";
	private static final String ARG_REFRESH = "autoRefresh";


	
	private Device device;
	private boolean isAutoRefresh;

	private DeviceInfoListAdapter mAdapter;
	private List<Map<String,Object>> dataList;
	private TextView deviceName_textView;



	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param device An instance of a device object
	 * @return A new instance of fragment DeviceInfoFragment.
	 */
	public static DeviceInfoFragment newInstance(Device device,boolean autoRefresh) {
		DeviceInfoFragment fragment = new DeviceInfoFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_DEVICE, device);
		args.putBoolean(ARG_REFRESH, autoRefresh);
		fragment.setArguments(args);
		return fragment;
	}

	public DeviceInfoFragment()
	{

	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		
		
		if (getArguments() != null) {
			device = getArguments().getParcelable(ARG_DEVICE);
			isAutoRefresh = getArguments().getBoolean(ARG_REFRESH);
		}
		
		if(device.getGtinArray()!=null)
		{	
			
			BigInteger gtin = BigInteger.valueOf(device.getGtinArray()[0] + device.getGtinArray()[1] * 256 + 
				device.getGtinArray()[2] * 65536 + device.getGtinArray()[3] * 16777216L + 
				device.getGtinArray()[4] * 4294967296L + device.getGtinArray()[5] * 1099511627776L);
			this.device.setGTIN(gtin);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_device_info, container, false);
	}



	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//mListener = (DeviceSetLocationListener) activity;
		
	}

	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		updateStampTextView = (TextView) getActivity().findViewById(R.id.deviceList_stamp_textView);
		
		
		//device name textview
		/*deviceName_textView = (TextView) getActivity().findViewById(R.id.fragment_device_info_name);
		deviceName_textView.setLongClickable(true);
		deviceName_textView.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View arg0) {
				//display dialog
				InputDialogFragment dialog = new InputDialogFragment();
				
				dialog.setType(InputDialogFragment.DEVICE_NAME);
				dialog.setHint(device.getLocation());
				dialog.show(getFragmentManager(), "setDeviceLocationDialog");
				return true;
			}
			
			
			
			
		});
		
		deviceName_textView.setText(device.getLocation().startsWith("?")? "Device Name: ? [Click and hold to rename device]" : device.getLocation()); // set device name textView text
		*/
		
		
		System.out.println(device.toString());
		
		//setup listener for item long click
		getListView().setOnItemLongClickListener(longClickListener);

//		dataList = getData(device);
        updateDevice(device,isAutoRefresh);




	}

	@Override
	public void onDetach() {
		super.onDetach();
		//mListener = null;
	}

	
	
	public List<Map<String,Object>> getData(Device device)
	{
		Resources res = getResources();
		
		//initial dataList
		if(dataList==null) {
			dataList = new ArrayList<Map<String,Object>>();
		}
		
		dataList.clear();
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		
		//device name	
		
		String location = device.getLocation();
		String name = location.startsWith("?")? location + "[Click and hold to name device]" : location;
		
		
		map.put("description", R.string.text_fragment_deviceInfo_deviceName);
		map.put("value", device==null? "n/a" : name);
			
		dataList.add(map);
		
		
		//device address
		map = new HashMap<String,Object>();
					
		//put correct address for device, address & 0b0011111 (63)
//		int address = device.getAddress() < 127 ? device.getAddress(): device.getAddress()-128;
		int address = device.getAddress() & 63;

		map.put("description", R.string.text_fragment_deviceInfo_address);
		map.put("value", device==null? "n/a" : address);
		
		dataList.add(map);
		
		map = new HashMap<String,Object>();
		
		map.put("description", R.string.text_fragment_deviceInfo_serialNumber);
		map.put("value", device==null? "n/a" : device.getSerialNumber());
			
		dataList.add(map);
		map = new HashMap<String,Object>();
		
		map.put("description", R.string.text_fragment_deviceInfo_gtin);
		map.put("value", device==null? "n/a" : device.getGTIN());
			
		dataList.add(map);
		/*
		 * map = new HashMap<String,Object>();
		 * map.put("description", "Location");
			map.put("value", device==null? "n/a" : device.getLocation());
		
			listDataSource.add(map);
		 * 
		 */
		
		
		map = new HashMap<String,Object>();
		
		map.put("description", R.string.text_fragment_deviceInfo_emergencyMode);
		map.put("value", device==null? "n/a" : getEmergencyText(device.getEmergencyModeStringIds()));
			
		dataList.add(map);
		map = new HashMap<String,Object>();
		
		map.put("description", R.string.text_fragment_deviceInfo_emergencyStatus);
		map.put("value", device==null? "n/a" : getEmergencyText(device.getEmergencyStatusStringIds()));
			
		dataList.add(map);
	
		map = new HashMap<String,Object>();
		
		map.put("description", R.string.text_fragment_deviceInfo_failureStatus);
		map.put("value", device==null? "n/a" : getEmergencyText(device.getFailureStringIds()));
			
		dataList.add(map);
		
		map = new HashMap<String,Object>();
		map.put("description",R.string.text_fragment_deviceInfo_batteryLevel);
		map.put("value", device==null? "n/a" : device.getBatteryLevel());
			
		dataList.add(map);
		
		map = new HashMap<String,Object>();
		map.put("description", R.string.text_fragment_deviceInfo_durationTest);
		String dtTimeText;
		if(device.isCommunicationStatus()){
			dtTimeText = res.getString(R.string.text_durationTest_value,device.getDtTime()); 
		}
		else{
			dtTimeText = "-";
		}
		
		map.put("value", device==null? "n/a" : dtTimeText);
			
		dataList.add(map);
		
		map = new HashMap<String,Object>();
		map.put("description", R.string.text_fragment_deviceInfo_emergencyLamp);
			
		String emgergencyLampText ;
		if(device.isCommunicationStatus()){
			emgergencyLampText= res.getString(R.string.text_emergencyLamp_value, device.getLampEmergencyTimeHour());
		}
		else{
			emgergencyLampText = "-";
		}
		
		map.put("value", device==null? "n/a" : emgergencyLampText);

		
		dataList.add(map);
		
		map = new HashMap<String,Object>();
		map.put("description", R.string.text_fragment_deviceInfo_communicationStatus);
		String communicationText = device.isCommunicationStatus()? res.getString(R.string.text_deviceCommunicationOk): res.getString(R.string.text_deviceLost);
		map.put("value", device==null? "n/a" : communicationText);
			
		dataList.add(map);
	
		return dataList;
	}
	
	
	protected String getEmergencyText(List<Integer> stringId){
		StringBuilder sb = new StringBuilder();
		Resources res = getResources();
		if(device.isCommunicationStatus()){
			for(int emStringId : stringId){
                //ignore Battery fully charged text
				if(emStringId != R.string.emergencyStatus_BATTERY_FULLY_CHARGED){
					String s = res.getString(emStringId);
					sb.append(s);
				}
			}
            //remove last "," ,if there no text in the string builder, simply append a "-"
			if(sb.length()>0){
                sb.deleteCharAt(sb.length()-1);
            } else sb.append("-");
			
			
		}else{
			sb.append("-");
		}
		return sb.toString();
	}
	
	

	
	// location long clicked listener to set location for device

	OnItemLongClickListener longClickListener = new OnItemLongClickListener(){

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int position, long id) {
			
			// if locaion long clicked
			if(position==0)
			{
		
				//display dialog
				InputDialogFragment dialog = new InputDialogFragment();
				dialog.setHint(device.getLocation());
				dialog.setType(InputDialogFragment.SET_DEVICE_NAME);
				dialog.show(getFragmentManager(), "inputDialog");
			}
	
			return true;
		}
		
		
		
	};

	
	
	public void updateLocation()
	{
		//update listDataSource
		//deviceName_textView.setText(device.getLocation());
		dataList = getData(device);
		mAdapter.notifyDataSetChanged();
		
	}
	
	/**
	 * @param device
	 */
	public void updateDevice(Device device, boolean autoRefresh) {


        if(dataList==null) {
            dataList = new ArrayList<Map<String,Object>>();
        }
        dataList.clear();
		
		//update refresh data stamp
		
		//Calendar deviceCal = device.getCal();
		
		//demo cal
		Calendar deviceCal = device.getCal();
		
    	SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getTimeInstance();
    	
    	String on = autoRefresh? "On" : "Off";
    	
    	String auto = getResources().getString(R.string.text_autoRefresh,on);
    	String time = getResources().getString(R.string.text_lastRefresh,sdf.format(deviceCal.getTime()));
    	    	
		updateStampTextView.setText(auto + time);
		
		//update deviceInfo ListView

		dataList = getData(device);

        if(mAdapter == null) {
            mAdapter = new DeviceInfoListAdapter(getActivity(), dataList, R.layout.device_info_row,
                    new String[]{"description", "value"}, new int[]{R.id.deviceDescription, R.id.deviceValue}, device);
            setListAdapter(mAdapter);
        }

		mAdapter.notifyDataSetChanged();
		
	}
	
}
