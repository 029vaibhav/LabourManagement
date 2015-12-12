package drawable.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.LeaderBoardDialogAdapter;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.enums.UserType;
import com.mobiles.mkshop.pojos.models.LeaderBoardDetails;

import org.joda.time.DateTime;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vaibhav on 15/11/15.
 */
public class UserSalesService extends Fragment implements View.OnClickListener {

    public static String TAG = "UserSalesService";

    RecyclerView recyclerView;
    TextView fromDate, toDate;
    String sToDate, sFromDate;
    DatePickerDialog fromDatePickerDialog, toDatePickerDialog;
    MaterialDialog materialDialog;
    String department;


    public static UserSalesService newInstance() {
        UserSalesService fragment = new UserSalesService();
        return fragment;
    }

    public UserSalesService() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.user_sales_service, container, false);
        initView(viewGroup);
        declareDepartment();


        return viewGroup;

    }

    private void declareDepartment() {

        if (MkShop.Role.equalsIgnoreCase(UserType.SALESMAN.name())) {
            department = "Sales";

        } else if (MkShop.Role.equalsIgnoreCase(UserType.TECHNICIAN.name())) {
            department = "Service";
        }
    }

    private void callService() {

        materialDialog.show();
        Client.INSTANCE.getUserSales(MkShop.AUTH, sToDate, sFromDate, MkShop.Username, department, new Callback<List<LeaderBoardDetails>>() {
            @Override
            public void success(List<LeaderBoardDetails> sales, Response response) {

                if (materialDialog != null && materialDialog.isShowing())
                    materialDialog.dismiss();

                LeaderBoardDialogAdapter leaderBoardDialogAdapter = new LeaderBoardDialogAdapter(UserSalesService.this, sales);
                recyclerView.setAdapter(leaderBoardDialogAdapter);

            }

            @Override
            public void failure(RetrofitError error) {

                if (materialDialog != null && materialDialog.isShowing())
                    materialDialog.dismiss();

                if (error.getKind().equals(RetrofitError.Kind.NETWORK))
                    MkShop.toast(getActivity(), "please check your internet connection");
                else MkShop.toast(getActivity(), error.getMessage());


            }
        });

    }


    private void initView(ViewGroup viewGroup) {

        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.recycler_view);
        fromDate = (TextView) viewGroup.findViewById(R.id.fromDate);
        toDate = (TextView) viewGroup.findViewById(R.id.toDate);
        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        fromDatePickerDialog = new DatePickerDialog(getActivity(), fromDateListener, DateTime.now().getYear(), DateTime.now().getMonthOfYear() - 1, DateTime.now().getDayOfMonth());
        toDatePickerDialog = new DatePickerDialog(getActivity(), toDateListner, DateTime.now().getYear(), DateTime.now().getMonthOfYear() - 1, DateTime.now().getDayOfMonth());
        materialDialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .cancelable(false)
                .build();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.toDate:

                if (fromDate.getText().toString().equalsIgnoreCase("from")) {
                    MkShop.toast(getActivity(), "please select starting date");
                } else {
                    toDatePickerDialog.show();
                }


                break;
            case R.id.fromDate:

                fromDatePickerDialog.show();
                break;
        }

    }


    DatePickerDialog.OnDateSetListener fromDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            if (view.isShown()) {

                DateTime dt = new DateTime(year, monthOfYear + 1, dayOfMonth, 01, 01);
                sFromDate = dt.toString("yyyy-MM-dd");
                fromDate.setText(dt.toString("dd-MM-YY"));

            }
        }
    };

    DatePickerDialog.OnDateSetListener toDateListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            if (view.isShown()) {

                DateTime dt = new DateTime(year, monthOfYear + 1, dayOfMonth, 01, 01);
                sToDate = dt.toString("yyyy-MM-dd");
                toDate.setText(dt.toString("dd-MM-YY"));

                callService();
            }

        }
    };


}
