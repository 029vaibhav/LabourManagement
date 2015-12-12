package com.mobiweb.expense.manager.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.mobiweb.expense.manager.Models.Employee;
import com.vaibhav.labour.management.R;
import com.mobiweb.expense.manager.utils.Global;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewEmployeeFragment extends Fragment implements View.OnClickListener {

    public static final String TAG="NewPaymentFragment";

    EditText name, mobile, address, workType;
    Global global;
    DB snappyDB;
    TextView submit;
    TextView cancel;
    String ID;
    Employee employee;

    public static ViewEmployeeFragment newInstance(String param1) {
        ViewEmployeeFragment fragment = new ViewEmployeeFragment();
        Bundle args = new Bundle();
        args.putString("id", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewEmployeeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ID = getArguments() != null ? getArguments().getString("id") : "";

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_new_employee, container, false);

        init(viewGroup);
        updateUI();


        return viewGroup;
    }

    private void updateUI() {

        try {
            snappyDB = global.openDB();
            employee = snappyDB.getObject(ID, Employee.class);
            name.setText(employee.getName());
            mobile.setText(employee.getPhone());
            address.setText(employee.getAddress());
            workType.setText(employee.getWorkType());

        } catch (SnappydbException e) {
            e.printStackTrace();
        }


    }

    private void init(ViewGroup viewGroup) {

        name = (EditText) viewGroup.findViewById(R.id.name);
        mobile = (EditText) viewGroup.findViewById(R.id.mobile);
        address = (EditText) viewGroup.findViewById(R.id.address);
        workType = (EditText) viewGroup.findViewById(R.id.work_type);
        submit = (TextView) viewGroup.findViewById(R.id.submit);
        submit.setText("Update");
        cancel = (TextView) viewGroup.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        submit.setOnClickListener(this);
        global = new Global(getActivity(),TAG);


    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.submit:
                validateData();

                break;
            case R.id.cancel:

                finishTheFragment();

                break;
        }


    }

    private void finishTheFragment() {

        global.closeDB();
        Fragment fragment = new EmployeesFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    private void validateData() {

        if (name.getText().length() == 0) {
            global.toast("please enter name");
        } else {
            saveDataToDB();
        }
    }

    private void saveDataToDB() {


        try {
            employee = updateEmployee();
            snappyDB.put(ID, employee);

        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        finishTheFragment();

    }

    private Employee updateEmployee() {

        employee.setName(name.getText().toString());
        employee.setAddress(address.getText().toString());
        employee.setPhone(mobile.getText().toString());
        employee.setWorkType(workType.getText().toString());
        return employee;

    }


}
