package drawable.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.enums.ProductType;
import com.mobiles.mkshop.pojos.models.BrandModelList;
import com.mobiles.mkshop.pojos.models.IncentiveEntity;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vaibhav on 31/7/15.
 */
public class NewIncentiveFragment extends Fragment {

    AutoCompleteTextView brand, model;
    EditText basePrice, quantity, incentive;
    Button submit;
    TextView validity;
    List<BrandModelList> salesList;
    List<BrandModelList> modelList;
    MaterialDialog materialDialog;
    String validityDate;

    public static NewIncentiveFragment newInstance(int pos) {
        NewIncentiveFragment fragment = new NewIncentiveFragment();
        return fragment;
    }

    public NewIncentiveFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_new_incentive, container, false);

        brand = (AutoCompleteTextView) viewGroup.findViewById(R.id.brandtext);
        model = (AutoCompleteTextView) viewGroup.findViewById(R.id.modeltext);
        basePrice = (EditText) viewGroup.findViewById(R.id.basePrice);
        quantity = (EditText) viewGroup.findViewById(R.id.quantity);
        incentive = (EditText) viewGroup.findViewById(R.id.incentiveAmount);
        validity = (TextView) viewGroup.findViewById(R.id.validity);
        submit = (Button) viewGroup.findViewById(R.id.submit);
        salesList = new ArrayList<>();
        modelList = new ArrayList<>();

        materialDialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .cancelable(false)
                .build();



                salesList.clear();
                materialDialog.dismiss();

                List<BrandModelList> sales = BrandModelList.listAll(BrandModelList.class);


                salesList = Lists.newArrayList(Iterables.filter(sales, new Predicate<BrandModelList>() {
                    @Override
                    public boolean apply(BrandModelList input) {
                        return (input.getType().equalsIgnoreCase(ProductType.Mobile.name()));
                    }
                }));


                List<String> brandStrings = new ArrayList<String>();
                Set<String> brands = new HashSet();
                for (int i = 0; i < sales.size(); i++) {
                    brands.add(sales.get(i).getBrand());
                }
                brandStrings.addAll(brands);

                ArrayAdapter<String> mobilelist = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, brandStrings);
                brand.setThreshold(1);
                brand.setAdapter(mobilelist);





        model.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (brand.getText().length() == 0)
                        MkShop.toast(getActivity(), "please select brand first");
                    else {

                        modelList = Lists.newArrayList(Iterables.filter(salesList, new Predicate<BrandModelList>() {
                            @Override
                            public boolean apply(BrandModelList input) {
                                return (input.getBrand().equalsIgnoreCase(brand.getText().toString()));
                            }
                        }));

                        List<String> brandList = new ArrayList<String>();
                        Set<String> brandStrings = new HashSet();
                        for (int i = 0; i < modelList.size(); i++) {
                            brandStrings.add(modelList.get(i).getModelNo());
                        }
                        brandList.addAll(brandStrings);


                        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                (getActivity(), android.R.layout.select_dialog_item, brandList);
                        model.setThreshold(1);
                        model.setAdapter(adapter);


                    }
                }
            }
        });


        validity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {


                        String sToDate = MkShop.checkDigit(i3) + "-" + MkShop.checkDigit(i2 + 1) + "-" + i;


                        validity.setText(sToDate);
                        DateTime dt = new DateTime(i, i2 + 1, i3, 01, 01);

                        validityDate = dt.toString("yyyy-MM-dd");


                    }
                }, DateTime.now().getYear(), DateTime.now().getMonthOfYear() - 1, DateTime.now().getDayOfMonth());
                datePickerDialog.show();

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (brand.getText().length() == 0)
                    MkShop.toast(getActivity(), "please select brand");

                else if (model.getText().length() == 0)
                    MkShop.toast(getActivity(), "please select model");

                else if (basePrice.getText().length() == 0)
                    MkShop.toast(getActivity(), "please enter base price");

                else if (quantity.getText().length() == 0)
                    MkShop.toast(getActivity(), "please mention quantity");

                else if (incentive.getText().length() == 0)
                    MkShop.toast(getActivity(), "please enter incentive amount");
                else if (validity.getText().length() == 0 || validity.getText().toString().equalsIgnoreCase("Validity"))
                    MkShop.toast(getActivity(), "please select date");
                else {

                    materialDialog.show();
                    IncentiveEntity incentiveEntity = new IncentiveEntity();
                    incentiveEntity.setBasePrice(basePrice.getText().toString());
                    incentiveEntity.setBrand(brand.getText().toString());
                    incentiveEntity.setModel(model.getText().toString());
                    incentiveEntity.setValidity(validityDate);
                    incentiveEntity.setQuantity(quantity.getText().toString());
                    incentiveEntity.setIncentiveAmount(incentive.getText().toString());

                    Client.INSTANCE.createIncentive(MkShop.AUTH, incentiveEntity, new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {
                            if (materialDialog != null && materialDialog.isShowing())
                            materialDialog.dismiss();
                            MkShop.toast(getActivity(), s);

                            Fragment fragment = getFragmentManager().findFragmentByTag(Incentive.TAG);
                            if (fragment == null) {
                                fragment = new Incentive();
                            }
                            getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if (materialDialog != null && materialDialog.isShowing())
                            materialDialog.dismiss();

                            if (error.getKind().equals(RetrofitError.Kind.NETWORK))
                                MkShop.toast(getActivity(), "Please check your internet connection");
                            else MkShop.toast(getActivity(), error.getMessage());

                        }
                    });
                }


            }
        });


        return viewGroup;
    }


}
