package com.mackwell.nlight_beta.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mackwell.nlight_beta.R;
import com.mackwell.nlight_beta.util.MySQLiteController;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class ListDialogFragment extends DialogFragment {
	
	public interface ListDialogListener{
		public void connectToPanels(List<Integer> selected);
		public void cancelDialog(List<Integer> selected);
		public void searchAgain();

		
	} 
	private ListView listView;
    private Map<String,Boolean> ipEnableMap;
    private Map<String,Boolean> ipCheckMap;
	private List<Map<String,Object>> dataList;
	private String[] ips; 												//An array contains panels' IP
	private ListDialogListener mListener; 								//A callback listener for dialog when button clicked
	private List<Integer> mSelectedItems = new ArrayList<Integer>();        	//a list contains item selected

    //sqlite
    private MySQLiteController mSqLiteController;

	public ListDialogFragment() {
		// Required empty public constructor
	}
	
	private class MyAdapter extends SimpleAdapter{
		
		
		Context mContext;
		List<? extends Map<String,?>> dataList;
		int resource;
		String[] from;
		int[] to;

		public MyAdapter(Context context, List<? extends Map<String, ?>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			this.mContext = context;
			this.resource = resource;
			this.dataList = data;
			this.from = from;
			this.to = to;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View rowView = inflater.inflate(resource, parent, false);

			TextView ipTextView = (TextView) rowView.findViewById(to[0]);
			CheckedTextView locationCheckedTextView = (CheckedTextView) rowView.findViewById(to[1]);

			String ip = (String) dataList.get(position).get(from[0]);
			ipTextView.setText(ip);

			String locationText = (String) dataList.get(position).get(from[1]);
			locationCheckedTextView.setText(locationText);

            //check enable and check and set row status
            //disable and un-check location check box and ip text
            if(!ipEnableMap.get(ip))
            {
                locationCheckedTextView.setChecked(false);
                locationCheckedTextView.setEnabled(false);
                ipTextView.setEnabled(false);

                locationCheckedTextView.setChecked(false);
                listView.setItemChecked(position, false);
            }else if(ipCheckMap.get(ip))
			{
                locationCheckedTextView.setChecked(true);
//                location.setEnabled(false);

                listView.setItemChecked(position, true);
            }

			return rowView;
		}
	}
	
	boolean check(String ip)
	{
		/*SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean save_checked = sp.getBoolean(SettingsActivity.SAVE_CHECKED, false);
		
		StringBuilder sb = new StringBuilder(ip);
		sb.append(" ");
		String ip_ = sb.toString();
		boolean check = sp.getBoolean(ip_, false);
		/*if(save_checked && check ) {
            return true;
        }
		else return false;*/
        //mSqLiteController.open();

        //boolean test = mSqLiteController.isEnable(ip);

		//mSqLiteController.close();
		//return true if ip is enabled as well as checked
//        return (save_checked && check && ipEnableMap.get(ip));

        mSqLiteController.open();
        boolean check = mSqLiteController.isChedked(ip);
        mSqLiteController.close();

        return check;


	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		listView = new ListView(getActivity());

        //enable scroll bar
        listView.setVerticalScrollBarEnabled(true);

        //SQLite
        mSqLiteController = new MySQLiteController(getActivity());

		//adapter
		SimpleAdapter mAdapter = new MyAdapter(getActivity(), getDataList(), R.layout.panel_list_row2, new String[]{"ip","location"},new int[]{R.id.ip_textview,R.id.location_checkedtextview});
		
		listView.setAdapter(mAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);		
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(R.id.location_checkedtextview);

                String ip = ips[position];

                //check ipEnableMap as well as check box status
                //if this panel has an error then it cannot be checked
				if(checkedTextView.isChecked() || !ipEnableMap.get(ip))
					checkedTextView.setChecked(false);

				else {
                    checkedTextView.setChecked(true);
                }
			}
		});
		

		//listView.setItemChecked(0, true);
		// Where we track the selected items
		
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    // Set the dialog title
	    builder.setTitle(ips == null? R.string.title_dialog_nopanelfound : R.string.title_dialog_panellist);


        //set dialog message panel is busy
        if(ipEnableMap.values().contains(false))
        {
            builder.setMessage(R.string.dialogmessage_panel_busy);
        }

	    
	    
	    
	    // Specify the ip array, the items to be selected by default (null for none),
	    // and the listener through which to receive callbacks when items are selected
	   /*builder.setMultiChoiceItems(ips, null,
	                      new DialogInterface.OnMultiChoiceClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int which,
	                       boolean isChecked) {
	                   if (isChecked) {
	                       // If the user checked the item, add it to the selected items
	                       mSelectedItems.add(which);
	                   } else if (mSelectedItems.contains(which)) {
	                       // Else, if the item is already in the array, remove it 
	                       mSelectedItems.remove(Integer.valueOf(which));
	                   }
	               }
	           });*/
	    
	    // Set the action buttons
	    builder.setView(listView);
	    
    		    builder.setNeutralButton(R.string.btn_searchagain, new DialogInterface.OnClickListener() {
				
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mListener.searchAgain();
					}
				});
	    
	           builder.setPositiveButton(R.string.btn_connect, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   // User clicked OK, so save the mSelectedItems results somewhere
	                   // or return them to the component that opened the dialog
	            	   
                       mSelectedItems = getCheckedItemsList(listView.getCheckedItemPositions()); // convert SparseBooleanMap to list
                       mListener.connectToPanels(mSelectedItems);
                       System.out.println(getCheckedItemsList(listView.getCheckedItemPositions()).toString());
	                   
	               }
	           });
	           builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   mSelectedItems = getCheckedItemsList(listView.getCheckedItemPositions()); // convert SparseBooleanMap to list
	            	   mListener.cancelDialog(mSelectedItems);            	   
	               }
	           });
	           
	       
	    
	    return builder.create();
	}

	@Override
	public void onAttach(Activity activity) {
		//attach mListener to the activity creates this dialog
		mListener = (ListDialogListener) activity;
		
		super.onAttach(activity);
	}

	/**
	 * Convert a SparseBooleanArray to an arraylist
	 * @param checkedItems a SparseBooleanArray of items checked
	 * @return an arraylist that contains items checked
	 */
	private ArrayList<Integer> getCheckedItemsList(SparseBooleanArray checkedItems){
		ArrayList<Integer> selected = new ArrayList<Integer>();
		int n = listView.getAdapter().getCount();
		
		for(int i = 0; i<n; i++)
		{
			System.out.println(i + " : " + checkedItems.get(i));
			if (checkedItems.get(i))
			{
				selected.add(i);
				
			}
			
		}
		
		return selected;
		
		
	}

	public String[] getIps() {
		return ips;
	}



	public void setIps(String[] ips) {
		this.ips = ips;
	}



	public List<Map<String, Object>> getDataList() {
		return dataList;
	}



	public void setDataList(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
	}

    public Map<String, Boolean> getIpEnableMap() {
        return ipEnableMap;
    }

    public void setIpEnableMap(Map<String, Boolean> ipEnableMap) {

        this.ipEnableMap = ipEnableMap;
    }

    public MySQLiteController getmSqLiteController() {
        return mSqLiteController;
    }

    public void setmSqLiteController(MySQLiteController mSqLiteController) {
        this.mSqLiteController = mSqLiteController;
    }

    public Map<String,Boolean> getIpCheckMap() {
        return ipCheckMap;
    }

    public void setIpCheckMap(Map<String,Boolean> ipCheckMap) {
        this.ipCheckMap = ipCheckMap;
    }
}
