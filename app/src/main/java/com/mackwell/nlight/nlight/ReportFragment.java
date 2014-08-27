package com.mackwell.nlight.nlight;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.mackwell.nlight.R;

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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView mListView;
    private SimpleAdapter mAdapter;


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
        mListView = (ListView) getActivity().findViewById(R.id.report_listView);

        mAdapter = new SimpleAdapter(getActivity(),getDataList(),R.layout.report_list_row,new String[] {"date","faults","status"},
                new int[] {R.id.report_date_textView,R.id.report_faults_textView,R.id.report_status_textView});
        mListView.setAdapter(mAdapter);

    }

    private List<Map<String,String>> getDataList(){

        ArrayList<Map<String,String>> dataList = new ArrayList<Map<String, String>>();

        HashMap<String,String> map = new HashMap<String, String>();
        map.put("date","Date/Time");
        map.put("faults","1");
        map.put("status","OK");
        dataList.add(map);

        map = new HashMap<String, String>();
        map.put("date","Date/Time");
        map.put("faults","2");
        map.put("status","NOT OK");
        dataList.add(map);

        map = new HashMap<String, String>();
        map.put("date","Date/Time");
        map.put("faults","32");
        map.put("status","NOT OK");
        dataList.add(map);


        return dataList;
    }
}
