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
import com.mobiweb.expense.manager.utils.Global;
import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.vaibhav.labour.management.R;
import com.mobiweb.expense.manager.adapters.EmployeeAdapter;

import java.util.ArrayList;
import java.util.List;


public class EmployeesFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "EmployeesFragment";

    FloatingActionButton fab;
    RecyclerView recyclerView;
    DB snappyDB;
    Global global;
    List<Employee> employees;

    public EmployeesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_employees, container, false);

        init(viewGroup);
        fab.setOnClickListener(this);

        retrieveDataFromDb();
        updateUI();

        return viewGroup;
    }

    private void updateUI() {

        EmployeeAdapter employeeAdapter = new EmployeeAdapter(employees, EmployeesFragment.this);
        recyclerView.setAdapter(employeeAdapter);
        global.dismissProgess();
    }

    private void retrieveDataFromDb() {

        snappyDB = global.openDB();

        try {
            for (String key : snappyDB.findKeys(getString(R.string.prefix_id))) {
                employees.add(snappyDB.getObject(key, Employee.class));
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        global.closeDB();

    }


    private void init(ViewGroup viewGroup) {

        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        fab = (FloatingActionButton) viewGroup.findViewById(R.id.fab);
        global = new Global(getActivity(), TAG);
        employees = new ArrayList<>();
        global.showProgress();


    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {

            case R.id.fab:
                Fragment fragment = new NewEmployeeFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                break;

        }
    }
}
