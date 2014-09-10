package com.mackwell.nlight_beta.activity;



import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mackwell.nlight_beta.R;
import com.mackwell.nlight_beta.models.Report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ReportFragment extends Fragment {

    public interface OnListItemClickedListener {

        public void onCLick(int position);
    }

    private static final String TAG= "Report Fragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnListItemClickedListener mListener;

    private List<Report> reportList;

    private ListView mListView;
    private SimpleAdapter mAdapter;

    private TextView textView;
    private ProgressBar progressBar;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public ReportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (OnListItemClickedListener) activity;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement ReportFragment.OnListItemClickedListener");

        }



    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        textView = (TextView) getActivity().findViewById(R.id.report_textView);
        progressBar = (ProgressBar) getActivity().findViewById(R.id.report_progressBar);


        mListView = (ListView) getActivity().findViewById(R.id.report_listView);

        mAdapter = new SimpleAdapter(getActivity(),getDataList(),R.layout.report_list_row,new String[] {"date","faults","status"},
                new int[] {R.id.report_date_textView,R.id.report_faults_textView,R.id.report_status_textView});
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i!=0) {
                    mListener.onCLick(i);
                }
            }

        });

    }

    private List<Map<String,String>> getDataList(){

        ArrayList<Map<String,String>> dataList = new ArrayList<Map<String, String>>();
        HashMap<String,String> map;

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");


        map = new HashMap<String, String>();
        map.put("date", "Date/Time");
        map.put("faults", "Fault(s) found");
        map.put("status", "Status");
        dataList.add(map);

        if (reportList!=null) {

            for (Report report : reportList) {

                map = new HashMap<String, String>();
                map.put("date", format1.format(report.getDate().getTime()));
                map.put("faults", Integer.toString(report.getFaults()));
                map.put("status", report.isFaulty() ? "Fault(s) found" : "OK");
                dataList.add(map);

            }

        }


        return dataList;
    }


    public void updateList(List<Report> reportList){
        Log.i(TAG,"update report list");
        this.reportList = reportList;
        mAdapter = new SimpleAdapter(getActivity(),getDataList(),R.layout.report_list_row,new String[] {"date","faults","status"},
                new int[] {R.id.report_date_textView,R.id.report_faults_textView,R.id.report_status_textView});
        mListView.setAdapter(mAdapter);

        hideLoading();


    }

    public void hideLoading(){
        progressBar.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
    }

    public void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
    }


}
