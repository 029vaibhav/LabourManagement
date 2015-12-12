package drawable.fragments;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.CustomAdapter;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.interfaces.ScannerCallback;
import com.mobiles.mkshop.pojos.enums.ProductType;
import com.mobiles.mkshop.pojos.models.BrandModelList;
import com.mobiles.mkshop.pojos.models.Sales;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.mobiles.mkshop.application.MkShop.toast;


public class SaleFragment extends Fragment implements ScannerCallback, View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    // TODO: Rename parameter arguments, choose names that match

    public static String TAG = "Sales";

    MaterialDialog materialDialog;
    private RadioGroup radiogroup;
    private TextView brand, accessoryType, modelNo, imeitextview, starCustomerName, starMobileNo, startImei;
    EditText quantity, price, other, customerName, imei, mobile;
    Button submit;
    List<BrandModelList> modelSalesList, productTypeList, salesList;
    List<String> brandList, modelList, accessoryTypeList;
    Dialog brandModelDialog;
    Scanner picker;
    String stringBrand = null, stringProductType = ProductType.Mobile.name(), stringAccessory = null, stringModel = null;
    TextView dialogTitle, scanImage;
    ListView dialogListView;


    public static SaleFragment newInstance(String brand, String model) {
        SaleFragment fragment = new SaleFragment();
        Bundle args = new Bundle();
        args.putString("brand", brand);
        args.putString("model", model);
        fragment.setArguments(args);

        return fragment;
    }

    public SaleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stringBrand = getArguments().getString("brand");
            stringModel = getArguments().getString("model");
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MkShop.SCRREN = "SaleFragment";
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_sale, container, false);
        initViews(v);


        if (getArguments() != null) {
            brand.setText(stringBrand);
            modelNo.setText(stringModel);
        }


        listInit();

        brand.setOnClickListener(this);
        accessoryType.setOnClickListener(this);
        modelNo.setOnClickListener(this);
        submit.setOnClickListener(this);
        radiogroup.setOnCheckedChangeListener(this);
        scanImage.setOnClickListener(this);

        return v;
    }

    private void listInit() {

        brandList = new ArrayList<>();
        productTypeList = new ArrayList<>();
        accessoryTypeList = new ArrayList<>();
        modelSalesList = new ArrayList<>();
        modelList = new ArrayList<>();
        salesList = BrandModelList.listAll(BrandModelList.class);


        try {
            productTypeList = Lists.newArrayList(Iterables.filter(salesList, new Predicate<BrandModelList>() {
                @Override
                public boolean apply(BrandModelList input) {
                    return (input.getType().equalsIgnoreCase(stringProductType));
                }
            }));
        } catch (Exception e) {
            Log.e("Err", e.getMessage());
        }


        Set<String> brands = new HashSet();
        for (int i = 0; i < productTypeList.size(); i++) {
            brands.add(productTypeList.get(i).getBrand());
        }
        brandList.addAll(brands);

    }

    private void initViews(ViewGroup v) {

        materialDialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .cancelable(false)
                .build();
        radiogroup = (RadioGroup) v.findViewById(R.id.radiogroup);
        brand = (TextView) v.findViewById(R.id.brandtext);
        accessoryType = (TextView) v.findViewById(R.id.accessoryType);
        modelNo = (TextView) v.findViewById(R.id.modeltext);
        quantity = (EditText) v.findViewById(R.id.quantityEdit);
        price = (EditText) v.findViewById(R.id.priceEdit);
        other = (EditText) v.findViewById(R.id.otheredit);
        submit = (Button) v.findViewById(R.id.submit);
        imeitextview = (TextView) v.findViewById(R.id.imeitextview);
        customerName = (EditText) v.findViewById(R.id.customerName);
        mobile = (EditText) v.findViewById(R.id.mobile);
        imei = (EditText) v.findViewById(R.id.imei);
        starCustomerName = (TextView) v.findViewById(R.id.star_customer_name);
        starMobileNo = (TextView) v.findViewById(R.id.star_mobile_no);
        startImei = (TextView) v.findViewById(R.id.star_imei);
        scanImage = (TextView) v.findViewById(R.id.scan_image);
        picker = new Scanner();
        picker.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
        picker.setCallBack(SaleFragment.this);

        brandModelDialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light_NoActionBar);
        brandModelDialog.setContentView(R.layout.dialog_layout);
        dialogTitle = (TextView) brandModelDialog.findViewById(R.id.dialogtitle);
        dialogListView = (ListView) brandModelDialog.findViewById(R.id.dialoglist);


    }

    @Override
    public void setIMEI(String imeiNumber) {

        if (imeiNumber != null)
            imei.setText(imeiNumber);
        picker.dismiss();

    }

    @Override
    public void onClick(View v) {


        int id = v.getId();


        switch (id) {
            case R.id.brandtext:

                brandClickListener();

                break;

            case R.id.modeltext:

                modelClickListener();

                break;

            case R.id.accessoryType:

                accessoryClickListener();
                break;

            case R.id.submit:
                submitClickListener();
                break;

            case R.id.scan_image:
                picker.show(getFragmentManager(), "imei scanner");

                break;
        }


    }

    private void submitClickListener() {


        if (stringProductType.equalsIgnoreCase(ProductType.Accessory.name()) && stringAccessory == null) {
            toast(getActivity(), "please select accessory type");

        } else if (stringBrand == null) {
            toast(getActivity(), "please select brand");

        } else if (modelNo.getText().length() == 0 || modelNo.getText().toString().equalsIgnoreCase("other") && other.getText().toString().length() == 0) {
            toast(getActivity(), "please select model");

        } else if (price.getText().length() <= 0) {
            toast(getActivity(), "please enter price");

        } else if (stringProductType.equalsIgnoreCase(ProductType.Mobile.name()) && customerName.getText().length() <= 0) {
            toast(getActivity(), "please enter customer name");

        } else if (stringProductType.equalsIgnoreCase(ProductType.Mobile.name()) && mobile.getText().length() != 10) {
            toast(getActivity(), "mobile no should be 10 digit");

        } else if (stringProductType.equalsIgnoreCase(ProductType.Mobile.name()) && imei.getText().length() == 0) {
            toast(getActivity(), "please enter imei");

        } else {
            if (stringModel.equalsIgnoreCase("other")) {
                stringModel = other.getText().toString();
            }


            new SendData().execute();


        }
    }

    private void accessoryClickListener() {


        dialogTitle.setText("Accessory ");
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), accessoryTypeList);
        dialogListView.setAdapter(customAdapter);
        dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {
                stringAccessory = accessoryTypeList.get(position);
                brandModelDialog.dismiss();
                accessoryType.setText(stringAccessory);

                List<BrandModelList> newArrayList = Lists.newArrayList(Iterables.filter(productTypeList, new Predicate<BrandModelList>() {
                    @Override
                    public boolean apply(BrandModelList input) {
                        return (input.getType().equalsIgnoreCase(stringProductType) && input.getAccessoryType().equalsIgnoreCase(stringAccessory));
                    }
                }));


                Set<String> brand = new HashSet();
                for (int i = 0; i < newArrayList.size(); i++) {
                    brand.add(newArrayList.get(i).getBrand());
                }
                brandList.addAll(brand);


            }
        });

        brandModelDialog.show();
    }

    private void modelClickListener() {
        if (stringBrand == null) {
            toast(getActivity(), "please select brand");
        } else {


            dialogTitle.setText("Model no");
            CustomAdapter customAdapter = new CustomAdapter(getActivity(), modelList);
            dialogListView.setAdapter(customAdapter);
            dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {
                    stringModel = modelList.get(position);
                    brandModelDialog.dismiss();
                    if (stringModel.equalsIgnoreCase("other")) {
                        other.setVisibility(View.VISIBLE);
                    } else {
                        other.setVisibility(View.GONE);
                        other.getText().clear();
                    }


                    modelNo.setText(stringModel);
                }
            });

            brandModelDialog.show();
        }
    }

    private void brandClickListener() {


        dialogTitle.setText("Brand");
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), brandList);
        dialogListView.setAdapter(customAdapter);
        dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {
                stringBrand = brandList.get(position);
                brandModelDialog.dismiss();
                brand.setText(stringBrand);
                modelSalesList = Lists.newArrayList(Iterables.filter(productTypeList, new Predicate<BrandModelList>() {
                    @Override
                    public boolean apply(BrandModelList input) {

                        if (stringAccessory == null)
                            return (input.getBrand().equalsIgnoreCase(stringBrand));
                        else
                            return (input.getBrand().equalsIgnoreCase(stringBrand) && input.getAccessoryType().equalsIgnoreCase(stringAccessory));


                    }
                }));

                modelList = new ArrayList<String>();
                for (int i = 0; i < modelSalesList.size(); i++) {
                    modelList.add(modelSalesList.get(i).getModelNo());
                }
                modelList.add("other");

            }
        });

        brandModelDialog.show();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {


        switch (checkedId) {
            case R.id.radiomobile:
                accessoryType.setVisibility(View.GONE);
                stringProductType = ProductType.Mobile.name();
                accessoryType.setText("");
                brand.setText("");
                modelNo.setText("");
                other.setText("");
                quantity.getText().clear();
                price.getText().clear();
                other.setVisibility(View.GONE);
                stringModel = null;
                stringAccessory = null;
                stringBrand = null;
                imeitextview.setVisibility(View.VISIBLE);
                imei.setVisibility(View.VISIBLE);
                starCustomerName.setVisibility(View.VISIBLE);
                starMobileNo.setVisibility(View.VISIBLE);
                startImei.setVisibility(View.VISIBLE);
                scanImage.setVisibility(View.VISIBLE);


                modelSalesList.clear();
                productTypeList.clear();
                modelList.clear();
                accessoryTypeList.clear();
                brandList.clear();
                productTypeList = Lists.newArrayList(Iterables.filter(salesList, new Predicate<BrandModelList>() {
                    @Override
                    public boolean apply(BrandModelList input) {
                        return (input.getType().equalsIgnoreCase(stringProductType));
                    }
                }));

                Set<String> brandStrings = new HashSet();
                for (int i = 0; i < productTypeList.size(); i++) {
                    brandStrings.add(productTypeList.get(i).getBrand());
                }
                brandList.addAll(brandStrings);


                break;
            case R.id.radioAccessory:
                accessoryType.setVisibility(View.VISIBLE);
                stringProductType = ProductType.Accessory.name();
                accessoryType.setText("");
                brand.setText("");
                modelNo.setText("");
                other.setText("");
                quantity.getText().clear();
                price.getText().clear();
                other.setVisibility(View.GONE);
                stringModel = null;
                stringAccessory = null;
                stringBrand = null;
                imeitextview.setVisibility(View.GONE);
                imei.setVisibility(View.GONE);
                starCustomerName.setVisibility(View.GONE);
                starMobileNo.setVisibility(View.GONE);
                startImei.setVisibility(View.GONE);
                scanImage.setVisibility(View.GONE);


                modelSalesList.clear();
                productTypeList.clear();
                modelList.clear();
                accessoryTypeList.clear();
                brandList.clear();
                productTypeList = Lists.newArrayList(Iterables.filter(salesList, new Predicate<BrandModelList>() {
                    @Override
                    public boolean apply(BrandModelList input) {
                        return (input.getType().equalsIgnoreCase(stringProductType));
                    }
                }));

                Set<String> accessoryStrings = new HashSet();
                for (int i = 0; i < productTypeList.size(); i++) {
                    accessoryStrings.add(productTypeList.get(i).getAccessoryType());
                }
                accessoryTypeList.addAll(accessoryStrings);

        }


    }


    private class SendData extends AsyncTask<Void, Void, Void> {


        Sales sales;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            materialDialog.show();


            sales = new Sales();
            sales.setType(stringProductType);
            sales.setBrand(stringBrand);
            sales.setModel(stringModel);
            if (stringAccessory == null)
                sales.setAccessoryType("");
            else
                sales.setAccessoryType(stringAccessory);
            sales.setQuantity("1");
            sales.setPrice(price.getText().toString());
            sales.setUsername(MkShop.Username);
            sales.setCustomerName(customerName.getText().toString());
            sales.setMobile(mobile.getText().toString());
            if (imei.getText().length() == 0)
                sales.setImei("");
            else
                sales.setImei(imei.getText().toString());
        }


        @Override
        protected Void doInBackground(Void... params) {


            Client.INSTANCE.sales(MkShop.AUTH, sales, new Callback<String>() {
                @Override
                public void success(String response, Response response2) {

                    MkShop.toast(getActivity(), response);
                    if (materialDialog != null && materialDialog.isShowing())
                        materialDialog.dismiss();

                    submit.setEnabled(false);


                }

                @Override
                public void failure(RetrofitError error) {

                    if (materialDialog != null && materialDialog.isShowing())
                        materialDialog.dismiss();


                    if (error.getKind().equals(RetrofitError.Kind.NETWORK))
                        MkShop.toast(getActivity(), "check your internet connection");
                    else
                        MkShop.toast(getActivity(), error.getMessage());

                    submit.setEnabled(true);

                }
            });

            return null;
        }
    }
}
