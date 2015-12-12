package com.mobiweb.expense.manager.adapters;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobiweb.expense.manager.Models.Employee;
import com.mobiweb.expense.manager.Models.EmployeeInstance;
import com.mobiweb.expense.manager.Models.PaymentData;
import com.mobiweb.expense.manager.fragments.EmployeesFragment;
import com.mobiweb.expense.manager.fragments.ViewEmployeeFragment;
import com.mobiweb.expense.manager.fragments.ViewPaymentFragment;
import com.mobiweb.expense.manager.utils.Global;
import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.vaibhav.labour.management.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaibhav on 12/12/15.
 */
public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {

    List<Employee> employees;
    Fragment context;
    AlertDialog.Builder builderSingle;
//    ArrayAdapter<String> arrayAdapter;
    CustomArrayAdapter customArrayAdapter;

    public EmployeeAdapter(List<Employee> employees, Fragment context) {
        this.employees = employees;
        this.context = context;
        init();

    }


    @Override
    public EmployeeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EmployeeAdapter.ViewHolder holder, int position) {


        Employee employee = employees.get(position);

        holder.name.setText(employee.getName());
        holder.mobile.setText(employee.getPhone());
        if (employee.getPaymentHistory() != null) {
            int total = 0;
            for (PaymentData data : employee.getPaymentHistory()) {
                total = total + data.getAmount();
            }
            employee.setTotalPayment(total);
        }
        holder.money.setText(context.getString(R.string.rs) + " " + employee.getTotalPayment());


    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public void init() {
        builderSingle = new AlertDialog.Builder(context.getActivity());
        builderSingle.setTitle("Select one of the options");

        ArrayList<String> strings = new ArrayList<>();
        strings.add("View/Update Profile");
        strings.add("View/Add Payments");
        strings.add("Delete User");

        customArrayAdapter = new CustomArrayAdapter(context.getActivity(), strings);

//        arrayAdapter = new ArrayAdapter<>(
//                context.getActivity(),
//                android.R.layout.select_dialog_singlechoice);
//        arrayAdapter.add("View/Update Profile");
//        arrayAdapter.add("View/Add Payments");
//        arrayAdapter.add("Delete User");
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardItemLayout;
        public TextView name, money, mobile;

        public ViewHolder(View itemView) {
            super(itemView);

            cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);
            name = (TextView) itemView.findViewById(R.id.name);
            money = (TextView) itemView.findViewById(R.id.money);
            mobile = (TextView) itemView.findViewById(R.id.mobile);
            cardItemLayout.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int id = v.getId();

            switch (id) {
                case R.id.cardlist_item:
                    if (builderSingle != null) {
                        initDialog();
                        builderSingle.show();
                    } else {
                        init();
                        initDialog();
                        builderSingle.show();
                    }
            }

        }

        private void initDialog() {


            builderSingle.setAdapter(customArrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Employee employee = employees.get(getAdapterPosition());

                    switch (which) {
                        case 0:
                            ViewEmployeeFragment fragment = ViewEmployeeFragment.newInstance(employee.getId());
                            context.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

                            break;
                        case 1:
                            EmployeeInstance.DATA.setEmployee(employee);
                            Fragment viewPaymentFragment = context.getActivity().getSupportFragmentManager().findFragmentByTag(ViewPaymentFragment.TAG);
                            if (viewPaymentFragment == null) {
                                viewPaymentFragment = new ViewPaymentFragment();
                            }
                            context.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, viewPaymentFragment).commit();

                            break;
                        case 2:
                            try {
                                Global global = new Global(context.getActivity(), "EmployeesFragment");
                                DB db = global.openDB();
                                db.del(employee.getId());
                                global.closeDB();
                                global.toast("record successfully deleted");

                            } catch (SnappydbException e) {
                                e.printStackTrace();
                            }
                            EmployeesFragment employeesFragment = new EmployeesFragment();
                            context.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, employeesFragment).commit();

                            break;
                    }

                    dialog.dismiss();

                }
            });

        }


    }

}