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
import com.mobiles.mkshop.adapters.RevenueCompartorAdapter;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.models.PriceCompartorService;

import org.joda.time.DateTime;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RevenueCompatorFragment extends Fragment {

    public static String TAG = "RevenueCompatorFragment";


    TextView toDate, fromDate, totalQuantity, totalRevenue, category;
    String sFromdate, sToDate;
    int tempQuantity = 0, tempRevenue = 0;
    RevenueCompartorAdapter listItemAdapter;
    RecyclerView listView;

    public static RevenueCompatorFragment newInstance(String param1, String param2) {
        RevenueCompatorFragment fragment = new RevenueCompatorFragment();

        return fragment;
    }

    public RevenueCompatorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_revenue_compator, container, false);

        fromDate = (TextView) viewGroup.findViewById(R.id.fromDate);
        toDate = (TextView) viewGroup.findViewById(R.id.toDate);
        category = (TextView) viewGroup.findViewById(R.id.category);

        totalQuantity = (TextView) viewGroup.findViewById(R.id.totalQuantity);
        totalRevenue = (TextView) viewGroup.findViewById(R.id.totalRevenue);
        listView = (RecyclerView) viewGroup.findViewById(R.id.saleslistview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        listView.setLayoutManager(linearLayoutManager);


        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (fromDate.getText().toString().equalsIgnoreCase("from") || toDate.getText().toString().equalsIgnoreCase("to")) {
                    MkShop.toast(getActivity(), "please select date range");
                } else {
                    {
                        new MaterialDialog.Builder(getActivity())
                                .items(R.array.revenueComparator)
                                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, final int which, CharSequence text) {

                                        tempQuantity = 0;
                                        tempRevenue = 0;
                                        category.setText(text.toString());

                                        Client.INSTANCE.getpricecompator(MkShop.AUTH, sFromdate, sToDate, "" + which, new Callback<List<PriceCompartorService>>() {
                                            @Override
                                            public void success(List<PriceCompartorService> response1, Response response) {


                                                listItemAdapter = new RevenueCompartorAdapter(getActivity(), response1);
                                                listView.setAdapter(listItemAdapter);


                                                for (int i = 0; i < response1.size(); i++) {
                                                    tempQuantity = tempQuantity + Integer.parseInt(response1.get(i).getQuantity());
                                                    if(response1.get(i).getPrice()!=null)
                                                    tempRevenue = tempRevenue + Integer.parseInt(response1.get(i).getPrice());
                                                }
                                                totalRevenue.setText("" + tempRevenue);
                                                totalQuantity.setText("" + tempQuantity);

                                            }

                                            @Override
                                            public void failure(RetrofitError error) {


                                                if (error.getKind().equals(RetrofitError.Kind.NETWORK))
                                                    MkShop.toast(getActivity(), "please check your internet connection");
                                                else
                                                    MkShop.toast(getActivity(), error.getMessage());

                                            }
                                        });

                                        return true;
                                    }
                                })
                                .positiveText("select")
                                .show();
                    }
                }

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


}
