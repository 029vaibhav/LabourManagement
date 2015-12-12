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
import com.mobiweb.expense.manager.Models.EmployeeInstance;
import com.mobiweb.expense.manager.Models.PaymentData;
import com.vaibhav.labour.management.R;
import com.mobiweb.expense.manager.utils.Global;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewPaymentFragment extends Fragment implements View.OnClickListener {

    public static final String TAG="NewPaymentFragment";

    EditText name, amount, modeOfPayment, date;
    Global global;
    DB snappyDB;
    TextView submit;
    TextView cancel;
    Employee employee;

    public NewPaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_payment_employee, container, false);

        init(viewGroup);


        return viewGroup;
    }

    private void init(ViewGroup viewGroup) {


        employee = EmployeeInstance.DATA.getEmployee();
        name = (EditText) viewGroup.findViewById(R.id.name);
        name.setText(employee.getName());
        amount = (EditText) viewGroup.findViewById(R.id.amount);
        modeOfPayment = (EditText) viewGroup.findViewById(R.id.mode_of_payment);
        date = (EditText) viewGroup.findViewById(R.id.date);
        date.setText(DateTime.now().toString("dd-MM-YY"));
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

        Fragment viewPaymentFragment = getActivity().getSupportFragmentManager().findFragmentByTag(ViewPaymentFragment.TAG);
        if (viewPaymentFragment == null) {
            viewPaymentFragment = new ViewPaymentFragment();
        }
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, viewPaymentFragment).commit();
    }

    private void validateData() {

        if (amount.getText().length() == 0) {
            global.toast("please enter name");
        } else if (modeOfPayment.getText().length() == 0) {
            global.toast("please enter mode of payment");
        } else {
            saveDataToDB();
        }
    }

    private void saveDataToDB() {

        snappyDB = global.openDB();

        try {

            PaymentData paymentData = createPayment();

            if (employee.getPaymentHistory() == null) {
                employee.setPaymentHistory(new ArrayList<PaymentData>());
            }

            employee.getPaymentHistory().add(paymentData);
            snappyDB.put(employee.getId(), employee);
            global.closeDB();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        finishTheFragment();

    }

    private PaymentData createPayment() {

        PaymentData paymentData = new PaymentData();
        paymentData.setDate(DateTime.now().toString("dd-MM-YY"));
        paymentData.setAmount(Integer.parseInt(amount.getText().toString()));
        paymentData.setModeOfPayment(modeOfPayment.getText().toString());

        return paymentData;

    }


}
