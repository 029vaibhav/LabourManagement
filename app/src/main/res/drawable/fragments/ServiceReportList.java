package drawable.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.ServiceReportItemAdapter;
import com.mobiles.mkshop.pojos.enums.Status;


public class ServiceReportList extends Fragment {


    private Status status;

    public static ServiceReportList newInstance() {
        ServiceReportList fragment = new ServiceReportList();

        return fragment;
    }

    public ServiceReportList() {
        // Required empty public constructor
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_service_report_list, container, false);
        RecyclerView recyclerView = (RecyclerView) viewGroup.findViewById(R.id.saleslistview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        ServiceReportItemAdapter listItemAdapter = new ServiceReportItemAdapter(this, status);
        recyclerView.setAdapter(listItemAdapter);


        return viewGroup;
    }


}
