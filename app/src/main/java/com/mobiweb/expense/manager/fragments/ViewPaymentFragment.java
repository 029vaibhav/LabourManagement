package com.mobiweb.expense.manager.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobiweb.expense.manager.Models.Employee;
import com.mobiweb.expense.manager.Models.EmployeeInstance;
import com.vaibhav.labour.management.R;
import com.mobiweb.expense.manager.adapters.PaymentAdapter;
import com.mobiweb.expense.manager.utils.Global;


public class ViewPaymentFragment extends Fragment implements View.OnClickListener {


    public final static String TAG="ViewPaymentFragment";
    FloatingActionButton fab;
    RecyclerView recyclerView;
    Employee employee;
    Global global;

    public ViewPaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_employees, container, false);

        init(viewGroup);
        fab.setOnClickListener(this);

        updateUI();

        return viewGroup;
    }

    private void updateUI() {


        employee = EmployeeInstance.DATA.getEmployee();
        PaymentAdapter employeeAdapter = new PaymentAdapter(employee.getPaymentHistory(), ViewPaymentFragment.this, employee);
        recyclerView.setAdapter(employeeAdapter);

    }


    private void init(ViewGroup viewGroup) {

        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        fab = (FloatingActionButton) viewGroup.findViewById(R.id.fab);
        global = new Global(getActivity(),TAG);


    }


    //change the code and update thhe comment
    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {

            case R.id.fab:
                Fragment fragment = new NewPaymentFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                break;

        }
    }
}
