package com.mobiweb.expense.manager.adapters;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobiweb.expense.manager.Models.PaymentData;
import com.mobiweb.expense.manager.utils.Global;
import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.mobiweb.expense.manager.Models.Employee;
import com.vaibhav.labour.management.R;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.List;

/**
 * Created by vaibhav on 12/12/15.
 */
public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    List<PaymentData> paymentDataList;
    Fragment context;
    AlertDialog.Builder builderSingle;
    ArrayAdapter<String> arrayAdapter;
    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-YY");
    Employee employee;


    public PaymentAdapter(List<PaymentData> employees, Fragment context, Employee employee) {

        if (employees != null)
            Collections.sort(employees);
        this.paymentDataList = employees;
        this.context = context;
        this.employee = employee;


    }


    @Override
    public PaymentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_recycler_item_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PaymentAdapter.ViewHolder holder, int position) {


        PaymentData paymentData = paymentDataList.get(position);

        holder.amount.setText(context.getString(R.string.rs) + " " + paymentData.getAmount());
        holder.modeOfPayment.setText(paymentData.getModeOfPayment());
        holder.date.setText(paymentData.getDate());
        holder.month.setText(formatter.parseDateTime(paymentData.getDate()).toString("MMM"));


    }

    @Override
    public int getItemCount() {

        if (paymentDataList == null) {
            return 0;
        } else
            return paymentDataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardItemLayout;
        public TextView amount, modeOfPayment, date, month;

        public ViewHolder(View itemView) {
            super(itemView);

            cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);
            amount = (TextView) itemView.findViewById(R.id.amount);
            modeOfPayment = (TextView) itemView.findViewById(R.id.mode_of_payment);
            date = (TextView) itemView.findViewById(R.id.date);
            month = (TextView) itemView.findViewById(R.id.month);

            cardItemLayout.setOnClickListener(this);
            initDialog();

        }

        @Override
        public void onClick(View v) {

            int id = v.getId();

            switch (id) {
                case R.id.cardlist_item:
                    if (builderSingle != null)
                        builderSingle.show();
                    else {
                        initDialog();
                        builderSingle.show();
                    }
            }

        }

        private void initDialog() {

            builderSingle = new AlertDialog.Builder(context.getActivity());
            builderSingle.setTitle("Select one of the options");

            arrayAdapter = new ArrayAdapter<>(
                    context.getActivity(),
                    android.R.layout.select_dialog_singlechoice);
            arrayAdapter.add("Remove payment");

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    employee.getPaymentHistory().remove(getAdapterPosition());
                    Global global = new Global(context.getActivity(), "ViewPaymentFragment");
                    DB db = global.openDB();
                    try {
                        db.put(employee.getId(), employee);
                        global.closeDB();
                        global.toast("successfully deleted");
                        notifyDataSetChanged();
                    } catch (SnappydbException e) {
                        e.printStackTrace();
                    }


                    dialog.dismiss();

                }
            });

        }


    }

}