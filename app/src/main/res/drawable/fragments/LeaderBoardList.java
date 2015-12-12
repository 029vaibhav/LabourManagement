package drawable.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.LeaderBoardItemAdapter;


public class LeaderBoardList extends Fragment {


    String department;
    int pt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pt = getArguments() != null ? getArguments().getInt("num") : 1;

        if (pt == 0) {
            department = "Sales";
        } else {
            department = "Service";
        }

    }

    public static LeaderBoardList newInstance(int pos) {
        LeaderBoardList fragment = new LeaderBoardList();
        Bundle args = new Bundle();
        args.putInt("num", pos);
        fragment.setArguments(args);

        return fragment;
    }

    public LeaderBoardList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_sales_report_list, container, false);
        RecyclerView listView = (RecyclerView)viewGroup.findViewById(R.id.saleslistview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        listView.setLayoutManager(linearLayoutManager);
        LeaderBoardItemAdapter listItemAdapter = new LeaderBoardItemAdapter(this, department);
        listView.setAdapter(listItemAdapter);


        return viewGroup;
    }


}
