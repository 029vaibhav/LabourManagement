package drawable.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.TabsPagerAdapterExpense;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.application.Myenum;
import com.mobiles.mkshop.pojos.enums.PaymentType;
import com.mobiles.mkshop.pojos.models.ExpenseEntity;

import org.joda.time.DateTime;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ViewExpenseFragment extends Fragment {

    private FragmentActivity myContext;
    public static String TAG = "ViewExpenseFragment";
    TextView toDate, fromDate, totalQuantity, totalRevenue;
    ViewPager viewPager;
    TabsPagerAdapterExpense adapter;
    String sFromdate, sToDate;
    int tempQuantity = 0, tempRevenue = 0;
    MaterialDialog materialDialog;

    public static ViewExpenseFragment newInstance() {
        ViewExpenseFragment fragment = new ViewExpenseFragment();
        return fragment;

    }

    public ViewExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {

        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MkShop.SCRREN = "ViewExpenseFragment";


        materialDialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .cancelable(false)
                .build();

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_view_expense, container, false);
        viewPager = (ViewPager) viewGroup.findViewById(R.id.pager);

        fromDate = (TextView) viewGroup.findViewById(R.id.fromDate);
        toDate = (TextView) viewGroup.findViewById(R.id.toDate);

        totalQuantity = (TextView) viewGroup.findViewById(R.id.totalQuantity);
        totalRevenue = (TextView) viewGroup.findViewById(R.id.totalRevenue);


        adapter = new TabsPagerAdapterExpense(myContext.getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) viewGroup.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                List<ExpenseEntity> sales1;
                tempQuantity = 0;
                tempRevenue = 0;

                if (position == 0) {


                    sales1 = Myenum.INSTANCE.getExpenseList(PaymentType.Product);
                    if (sales1 != null) {
                        for (int i = 0; i < sales1.size(); i++) {
                            tempQuantity = tempQuantity + Integer.parseInt(sales1.get(i).getQuantity());
                            tempRevenue = tempRevenue + Integer.parseInt(sales1.get(i).getAmount());
                        }
                        totalRevenue.setText("" + tempRevenue);
                        totalQuantity.setText("" + tempQuantity);
                    }

                } else if (position == 1) {
                    sales1 = Myenum.INSTANCE.getExpenseList(PaymentType.Salary);

                    if (sales1 != null) {
                        for (int i = 0; i < sales1.size(); i++) {
                        //    tempQuantity = tempQuantity + Integer.parseInt(sales1.get(i).getQuantity());
                            tempRevenue = tempRevenue + Integer.parseInt(sales1.get(i).getAmount());
                        }
                        totalRevenue.setText("" + tempRevenue);
                        totalQuantity.setText("");
                    }
                } else if (position == 2) {
                    sales1 = Myenum.INSTANCE.getExpenseList(PaymentType.Incentive);

                    if (sales1 != null) {
                        for (int i = 0; i < sales1.size(); i++) {
                       //     tempQuantity = tempQuantity + Integer.parseInt(sales1.get(i).getQuantity());
                            tempRevenue = tempRevenue + Integer.parseInt(sales1.get(i).getAmount());
                        }
                        totalRevenue.setText("" + tempRevenue);
                        totalQuantity.setText("");
                    }
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (fromDate.getText().toString().equalsIgnoreCase("from")) {
                    MkShop.toast(getActivity(), "please select starting date");
                } else {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {


                            String date = MkShop.checkDigit(i3) + "-" + MkShop.checkDigit(i2 + 1) + "-" + i;


                            toDate.setText(date);
                            DateTime dt = new DateTime(i, i2 + 1, i3, 01, 01);

                            sToDate = dt.toString("yyyy-MM-dd");

                            executeQuery();


                        }
                    }, DateTime.now().getYear(), DateTime.now().getMonthOfYear() - 1, DateTime.now().getDayOfMonth());
                    datePickerDialog.show();
                }
            }
        });


        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {


                        String date = MkShop.checkDigit(i3) + "-" + MkShop.checkDigit(i2 + 1) + "-" + i;


                        fromDate.setText(date);
                        DateTime dt = new DateTime(i, i2 + 1, i3, 01, 01);
                        sFromdate = dt.toString("yyyy-MM-dd");
                    }
                }, DateTime.now().getYear(), DateTime.now().getMonthOfYear() - 1, DateTime.now().getDayOfMonth());
                datePickerDialog.show();
            }
        });


        return viewGroup;
    }

    private void executeQuery() {

        materialDialog.show();


        Client.INSTANCE.getExpenseReport(MkShop.AUTH, sFromdate, sToDate, new Callback<List<ExpenseEntity>>() {
            @Override
            public void success(final List<ExpenseEntity> expenseEntities, Response response) {

                if (materialDialog != null && materialDialog.isShowing())
                    materialDialog.dismiss();
                Myenum.INSTANCE.setExpenseList(expenseEntities);
                if (materialDialog != null && materialDialog.isShowing())
                    materialDialog.dismiss();
                adapter = new TabsPagerAdapterExpense(myContext.getSupportFragmentManager());
                viewPager.setAdapter(adapter);

                tempQuantity = 0;
                tempRevenue = 0;

//                List<ExpenseEntity> sales1 = Myenum.INSTANCE.getExpenseList(PaymentType.Product);
//                for (int i = 0; i < sales1.size(); i++) {
//                    tempQuantity = tempQuantity + Integer.parseInt(sales1.get(i).getQuantity());
//                    tempRevenue = tempRevenue + Integer.parseInt(sales1.get(i).getAmount());
//                }
                totalRevenue.setText("" + tempRevenue);
                totalQuantity.setText("" + tempQuantity);


            }

            @Override
            public void failure(RetrofitError error) {
                if (materialDialog != null && materialDialog.isShowing())
                    materialDialog.dismiss();
                MkShop.toast(getActivity(), error.getMessage().toString());

            }
        });


    }




}
