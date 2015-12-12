package drawable.fragments;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.enums.UserType;
import com.mobiles.mkshop.pojos.models.BrandModelList;
import com.mobiles.mkshop.pojos.models.ServiceCenterEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class RepairNewItemFragment extends Fragment {

    public static String TAG = "RepairNewItemFragment";
    EditText price, other, problem;
    Button submit;
    String Stringdate = "", stringModel, stringBrand, stringStatus;
    int index;
    ServiceCenterEntity service;
    DatePickerDialog datePickerDialog;
    EditText jobNo;
    //  private RadioGroup radiogroup;
    private TextView  status;
//    TextView date
    AutoCompleteTextView brand, modelNo;
//    TextView dateTitle;
    List<BrandModelList> salesList, modelSalesList;
    List<String> brandList;

    MaterialDialog materialDialog;


    public RepairNewItemFragment() {
        // Required empty public constructor
    }

    public static RepairNewItemFragment newInstance() {
        RepairNewItemFragment fragment = new RepairNewItemFragment();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        MkShop.SCRREN = "RepairNewItemFragment";


        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_repair_new_item, container, false);


        modelSalesList = new ArrayList<>();
        brandList = new ArrayList<>();
        salesList = new ArrayList<>();


        materialDialog = new MaterialDialog.Builder(getActivity())
                .content("please wait")
                .progress(true, 0)
                .build();


        brand = (AutoCompleteTextView) v.findViewById(R.id.brandtext);
        status = (TextView) v.findViewById(R.id.status);
//        date = (TextView) v.findViewById(R.id.datetext);
        modelNo = (AutoCompleteTextView) v.findViewById(R.id.modeltext);
        price = (EditText) v.findViewById(R.id.priceEdit);
        other = (EditText) v.findViewById(R.id.otheredit);
        jobNo = (EditText) v.findViewById(R.id.jobnoedit);
        problem = (EditText) v.findViewById(R.id.problemedit);
        submit = (Button) v.findViewById(R.id.submit);
//        dateTitle = (TextView) v.findViewById(R.id.dateTitle);


        salesList = BrandModelList.listAll(BrandModelList.class);
        List<BrandModelList> sales = BrandModelList.listAll(BrandModelList.class);
        brandList.clear();
        Set<String> brandStrings = new HashSet();
        for (int i = 0; i < sales.size(); i++) {
            brandStrings.add(sales.get(i).getBrand());
        }
        brandList.addAll(brandStrings);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.select_dialog_item, brandList);
        brand.setThreshold(1);
        brand.setAdapter(adapter);


        modelNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (brand.getText().length() == 0)
                        MkShop.toast(getActivity(), "please select brand first");
                    else {
                        brandList.clear();
                        modelSalesList = Lists.newArrayList(Iterables.filter(salesList, new Predicate<BrandModelList>() {
                            @Override
                            public boolean apply(BrandModelList input) {
                                return (input.getBrand().equalsIgnoreCase(brand.getText().toString()));
                            }
                        }));


                        Set<String> brandStrings = new HashSet();
                        for (int i = 0; i < modelSalesList.size(); i++) {
                            brandStrings.add(modelSalesList.get(i).getModelNo());
                        }
                        brandList.addAll(brandStrings);


                        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                (getActivity(), android.R.layout.select_dialog_item, brandList);
                        modelNo.setThreshold(1);
                        modelNo.setAdapter(adapter);


                    }
                }
            }
        });

//        date.setOnClickListener(new View.OnClickListener() {
//
//
//            @Override
//            public void onClick(View v) {
//
//                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//
//                        Stringdate = "" + dayOfMonth + "-" + monthOfYear + "-" + year;
//                        date.setText("" + dayOfMonth + "/" + monthOfYear);
//                        datePickerDialog.dismiss();
//                        Stringdate = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
//
//                    }
//                }, DateTime.now().getYear(), DateTime.now().getMonthOfYear() - 1, DateTime.now().getDayOfMonth());
//                datePickerDialog.show();
//            }
//        });


        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .items(R.array.items)
                        .itemsCallbackSingleChoice(index, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                if (text != null) {
                                    stringStatus = text.toString();
//                                    if (!stringStatus.equalsIgnoreCase("Pending") || !stringStatus.equalsIgnoreCase("Delivered") || !stringStatus.equalsIgnoreCase("Returned")) {
//                                        date.setVisibility(View.VISIBLE);
//                                        dateTitle.setVisibility(View.VISIBLE);
//                                    } else {
//                                        date.setVisibility(View.GONE);
//                                        dateTitle.setVisibility(View.GONE);
//                                    }

                                    if (stringStatus.equalsIgnoreCase("Returned")) {
                                        price.setVisibility(View.GONE);
                                        price.setText("0");
                                    } else {
                                        price.setVisibility(View.VISIBLE);
                                    }
                                    status.setText(stringStatus);
                                    setindex(stringStatus);
                                }
                                return true;
                            }
                        })
                        .positiveText("select")
                        .show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (brand.getText().length() == 0) {
                    MkShop.toast(getActivity(), "please select brand");

                } else if (modelNo.getText().toString().length() == 0) {
                    MkShop.toast(getActivity(), "please select model");

                }
//
//                else if (!stringStatus.equalsIgnoreCase("Pending") && date.getText().length() <= 0 || date.getText().toString().equalsIgnoreCase("date")) {
//                    MkShop.toast(getActivity(), "please select date");
//
//                }
//

                else if (price.getText().length() <= 0) {
                    MkShop.toast(getActivity(), "please enter price");

                } else {

                    service = new ServiceCenterEntity();
                    service.setBrand(brand.getText().toString().trim());
                    service.setModel(modelNo.getText().toString().trim());
                    service.setStatus(stringStatus);
                    service.setPrice("" + price.getText().toString().trim());
                    service.setJobNo("" + jobNo.getText().toString().trim());
                    service.setDeliveryDate("");

                    if (MkShop.Role.equalsIgnoreCase(UserType.RECEPTIONIST.name()) || MkShop.Role.equalsIgnoreCase(UserType.SALESMAN.name())) {
                        service.setPlace("SP");
                    } else if (MkShop.Role.equalsIgnoreCase(UserType.TECHNICIAN.name())) {
                        service.setPlace("SC");
                    }
                    service.setProblem(problem.getText().toString());
                    service.setUsername(MkShop.Username);

                    new SendData().execute();
                }
            }
        });


        return v;
    }

    private int setindex(String status) {


        if (status.equalsIgnoreCase("Pending")) {
            index = 0;
        } else if (status.equalsIgnoreCase("Processing")) {
            index = 1;
        } else if (status.equalsIgnoreCase("Pna")) {

            index = 2;
        } else if (status.equalsIgnoreCase("Done")) {
            index = 3;
        } else if (status.equalsIgnoreCase("Return")) {
            index = 4;
        } else if (status.equalsIgnoreCase("Delivered")) {
            index = 5;
        } else if (status.equalsIgnoreCase("Returned")) {
            index = 6;
        }
        return index;

    }

    private class SendData extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            materialDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(Void... params) {


            Client.INSTANCE.sendService(MkShop.AUTH, service, new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {
                            MkShop.toast(getActivity(), s);
                            if (materialDialog != null && materialDialog.isShowing())
                                materialDialog.dismiss();

                            materialDialog.dismiss();
                            Fragment fragment = new RequestRepair();
                            getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if (materialDialog != null && materialDialog.isShowing())
                                materialDialog.dismiss();
                            if (error.getKind().equals(RetrofitError.Kind.NETWORK))
                                MkShop.toast(getActivity(), "please check your internet connection");
                            else MkShop.toast(getActivity(), error.getMessage());

                        }
                    }

            );
            return null;
        }
    }
}
