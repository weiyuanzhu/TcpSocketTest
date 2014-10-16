package com.mackwell.nlight_beta.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mackwell.nlight_beta.R;

import java.util.List;
import java.util.Map;

/**
 * Created by weiyuan zhu on 15/10/14.
 */
public class CachedPanelListAdapter extends BaseAdapter {


    class ViewHolder{
        CheckBox ip;
        TextView macAddress;
        TextView location;

    }

    private Context mContext;
    private Cursor mCursor;

    private List<Map<String, String>> mDataList;
    private LayoutInflater mInflater;

    public CachedPanelListAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = convertView;

        if(rowView==null){
            rowView = mInflater.inflate(R.layout.panel_list_row3, parent,false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.ip = (CheckBox) rowView.findViewById(R.id.cached_panel_list_ip_textView);
            viewHolder.macAddress = (TextView) rowView.findViewById(R.id.cached_panel_list_macAddress_textView);
            viewHolder.location = (TextView) rowView.findViewById(R.id.cached_panel_list_location_textView);
            rowView.setTag(viewHolder);
        }


        ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        mCursor.moveToPosition(position);

        String ipText = (String) mDataList.get(position).get("ip");
        String macText = (String) mDataList.get(position).get("mac");
        String locationText = (String) mDataList.get(position).get("location");


        viewHolder.ip.setText(ipText);
        viewHolder.ip.setFocusable(false);
        viewHolder.macAddress.setText(macText);
        viewHolder.location.setText(locationText);

        return rowView;
    }
}
