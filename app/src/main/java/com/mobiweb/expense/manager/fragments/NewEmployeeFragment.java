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

import org.joda.time.DateTime;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewEmployeeFragment extends Fragment implements View.OnClickListener {

    public static final String TAG="NewEmployeeFragment";

    EditText name, mobile, address, workType;
    Global global;
    DB snappyDB;
    TextView submit;
    TextView cancel;

    public NewEmployeeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_new_employee, container, false);

        init(viewGroup);


        return viewGroup;
    }

    private void init(ViewGroup viewGroup) {

        name = (EditText) viewGroup.findViewById(R.id.name);
        mobile = (EditText) viewGroup.findViewById(R.id.mobile);
        address = (EditText) viewGroup.findViewById(R.id.address);
        workType = (EditText) viewGroup.findViewById(R.id.work_type);
        submit = (TextView) viewGroup.findViewById(R.id.submit);
        submit.setOnClickListener(this);
        cancel = (TextView) viewGroup.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
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

        snappyDB = global.openDB();

        try {
            int i = snappyDB.countKeys(getString(R.string.prefix_id));
            Employee employee = createNewEmployee(i);
            snappyDB.put(global.makeNewId(i), employee);
            global.closeDB();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        finishTheFragment();

    }

    private Employee createNewEmployee(int i) {

        Employee employee = new Employee();
        employee.setName(name.getText().toString());
        employee.setAddress(address.getText().toString());
        employee.setPhone(mobile.getText().toString());
        employee.setWorkType(workType.getText().toString());
        employee.setId(global.makeNewId(i));
        employee.setDate(DateTime.now().toString("dd-MM-YY"));
        return employee;

    }


}
