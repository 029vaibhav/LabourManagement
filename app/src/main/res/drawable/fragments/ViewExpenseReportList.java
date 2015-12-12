package drawable.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.ViewExpenseItemAdapter;
import com.mobiles.mkshop.pojos.enums.PaymentType;


public class ViewExpenseReportList extends Fragment {


    PaymentType paymentType;
    int pt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pt = getArguments() != null ? getArguments().getInt("num") : 1;

        if (pt == 0) {
            paymentType = PaymentType.Salary;
        } else if (pt == 1) {
            paymentType = PaymentType.Incentive;
        }


    }

    public static ViewExpenseReportList newInstance(int pos) {
        ViewExpenseReportList fragment = new ViewExpenseReportList();
        Bundle args = new Bundle();
        args.putInt("num", pos);
        fragment.setArguments(args);

        return fragment;
    }

    public ViewExpenseReportList() {
        // Required empty public constructor
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_sales_report_list, container, false);
        RecyclerView recyclerView = (RecyclerView) viewGroup.findViewById(R.id.saleslistview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);


        ViewExpenseItemAdapter listItemAdapter = new ViewExpenseItemAdapter(this, paymentType);
        recyclerView.setAdapter(listItemAdapter);


        return viewGroup;
    }


}
