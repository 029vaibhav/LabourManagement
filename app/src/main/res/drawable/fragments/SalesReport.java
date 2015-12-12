package drawable.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.SalesReportItemAdapter;
import com.mobiles.mkshop.adapters.TabsPagerAdapter;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.application.Myenum;
import com.mobiles.mkshop.pojos.enums.ProductType;
import com.mobiles.mkshop.pojos.models.Sales;

import org.joda.time.DateTime;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SalesReport extends Fragment {

    private FragmentActivity myContext;
    public static String TAG = "SalesReport";
    Boolean quantityBoolean = false;
    Boolean priceBoolean = false;
    TextView toDate, fromDate, totalQuantity, totalRevenue;
    ViewPager viewPager;
    List<Sales> salesList;
    TabsPagerAdapter adapter;
    String sFromdate, sToDate;
    int tempQuantity = 0, tempRevenue = 0;
    MaterialDialog materialDialog;

    public static SalesReport newInstance() {
        SalesReport fragment = new SalesReport();
        return fragment;

    }

    public SalesReport() {
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
        MkShop.SCRREN = "SalesReport";


        materialDialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .cancelable(false)
                .build();

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_sales_report, container, false);
        viewPager = (ViewPager) viewGroup.findViewById(R.id.pager);
        TextView quantity = (TextView) viewGroup.findViewById(R.id.quantity);
        TextView revenue = (TextView) viewGroup.findViewById(R.id.revenue);
        fromDate = (TextView) viewGroup.findViewById(R.id.fromDate);
        toDate = (TextView) viewGroup.findViewById(R.id.toDate);

        totalQuantity = (TextView) viewGroup.findViewById(R.id.totalQuantity);
        totalRevenue = (TextView) viewGroup.findViewById(R.id.totalRevenue);


        adapter = new TabsPagerAdapter(myContext.getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) viewGroup.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                List<Sales> sales1;
                tempQuantity = 0;
                tempRevenue = 0;

                if (position == 0) {


                    sales1 = Myenum.INSTANCE.getSalesList(ProductType.Mobile);
                    if (sales1 != null) {
                        for (int i = 0; i < sales1.size(); i++) {
                            tempQuantity = tempQuantity + Integer.parseInt(sales1.get(i).getQuantity());
                            tempRevenue = tempRevenue + Integer.parseInt(sales1.get(i).getPrice());
                        }
                        totalRevenue.setText("" + tempRevenue);
                        totalQuantity.setText("" + tempQuantity);
                    }

                } else if (position == 1) {
                    sales1 = Myenum.INSTANCE.getSalesList(ProductType.Accessory);

                    if (sales1 != null) {
                        for (int i = 0; i < sales1.size(); i++) {
                            tempQuantity = tempQuantity + Integer.parseInt(sales1.get(i).getQuantity());
                            tempRevenue = tempRevenue + Integer.parseInt(sales1.get(i).getPrice());
                        }
                        totalRevenue.setText("" + tempRevenue);
                        totalQuantity.setText("" + tempQuantity);
                    }
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityBoolean = !quantityBoolean;

                int index = viewPager.getCurrentItem();
                TabsPagerAdapter adapter = ((TabsPagerAdapter) viewPager.getAdapter());
                SalesReportList fragment = adapter.getFragment(index);
                RecyclerView listView = (RecyclerView) fragment.getView().findViewById(R.id.saleslistview);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                listView.setLayoutManager(linearLayoutManager);

                SalesReportItemAdapter adapter2 = (SalesReportItemAdapter) listView.getAdapter();
                adapter2.sortquantiy(quantityBoolean);


            }
        });


        revenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priceBoolean = !priceBoolean;
                int index = viewPager.getCurrentItem();
                TabsPagerAdapter adapter = ((TabsPagerAdapter) viewPager.getAdapter());
                SalesReportList fragment = adapter.getFragment(index);
                RecyclerView listView = (RecyclerView) fragment.getView().findViewById(R.id.saleslistview);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                listView.setLayoutManager(linearLayoutManager);
                SalesReportItemAdapter adapter2 = (SalesReportItemAdapter) listView.getAdapter();
                adapter2.sortprice(priceBoolean);
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

                            if (datePicker.isShown()) {
                                String date = MkShop.checkDigit(i3) + "-" + MkShop.checkDigit(i2 + 1) + "-" + i;


                                toDate.setText(date);
                                DateTime dt = new DateTime(i, i2 + 1, i3, 01, 01);

                                sToDate = dt.toString("yyyy-MM-dd");

                                executeQuery();
                            }


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


        Client.INSTANCE.getSalesReport(MkShop.AUTH, sFromdate, sToDate, new Callback<List<Sales>>() {
            @Override
            public void success(final List<Sales> sales, Response response) {

                if (materialDialog != null && materialDialog.isShowing())
                    materialDialog.dismiss();
                Myenum.INSTANCE.setSalesList(sales);
                if (materialDialog != null && materialDialog.isShowing())
                    materialDialog.dismiss();
                adapter = new TabsPagerAdapter(myContext.getSupportFragmentManager());
                viewPager.setAdapter(adapter);

                tempQuantity = 0;
                tempRevenue = 0;

                List<Sales> sales1 = Myenum.INSTANCE.getSalesList(ProductType.Mobile);
                for (int i = 0; i < sales1.size(); i++) {
                    tempQuantity = tempQuantity + Integer.parseInt(sales1.get(i).getQuantity());
                    tempRevenue = tempRevenue + Integer.parseInt(sales1.get(i).getPrice());
                }
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
