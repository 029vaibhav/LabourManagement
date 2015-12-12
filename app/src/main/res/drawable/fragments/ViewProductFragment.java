package drawable.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.ViewProductadapter;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.enums.ProductType;
import com.mobiles.mkshop.pojos.models.Sales;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ViewProductFragment extends Fragment {

    public static String TAG = "ViewProductFragment";

    MaterialDialog dialog;
    AutoCompleteTextView brandTextView, search;
    TextView submit;
    RecyclerView gridRecyclerView;
    ScrollView scrollView;
    List<Sales> salesList;


    public static ViewProductFragment newInstance() {
        ViewProductFragment fragment = new ViewProductFragment();

        return fragment;
    }

    public ViewProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_view_product, container, false);

        salesList = new ArrayList<>();
        brandTextView = (AutoCompleteTextView) viewGroup.findViewById(R.id.brand);
        scrollView = (ScrollView) viewGroup.findViewById(R.id.scroll);
        gridRecyclerView = (RecyclerView) viewGroup.findViewById(R.id.recyclerView);

        search = (AutoCompleteTextView) viewGroup.findViewById(R.id.search);
        submit = (TextView) viewGroup.findViewById(R.id.submit);
        dialog = new MaterialDialog.Builder(getActivity())
                .content("please wait")
                .progress(true, 0)
                .show();

        Client.INSTANCE.getproduct(MkShop.AUTH, new Callback<List<Sales>>() {
            @Override
            public void success(List<Sales> sales, Response response) {

//                salesList = sales;

                salesList = Lists.newArrayList(Iterables.filter(sales, new Predicate<Sales>() {
                    @Override
                    public boolean apply(Sales input) {
                        return (input.getType().equalsIgnoreCase(ProductType.Mobile.name()));
                    }
                }));

                List<String> brandStrings = new ArrayList<String>();
                Set<String> brand = new HashSet();
                for (int i = 0; i < sales.size(); i++) {
                    brand.add(sales.get(i).getBrand().trim());
                }
                brandStrings.addAll(brand);

                dialog.dismiss();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (getActivity(), android.R.layout.select_dialog_item, brandStrings);
                brandTextView.setThreshold(1);
                brandTextView.setAdapter(adapter);


                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (brandTextView.getText().length() <= 0) {
                            MkShop.toast(getActivity(), "please select brand");
                        } else {

                            dialog.show();

                            List<Sales> modelListByBrand = Lists.newArrayList(Iterables.filter(salesList, new Predicate<Sales>() {
                                @Override
                                public boolean apply(Sales input) {
                                    return (input.getBrand().equalsIgnoreCase(brandTextView.getText().toString()));
                                }
                            }));
                            scrollView.setVisibility(View.GONE);
                            search.setVisibility(View.VISIBLE);
                            gridRecyclerView.setVisibility(View.VISIBLE);
                            gridRecyclerView.setHasFixedSize(true);
                            gridRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                            final ViewProductadapter listItemAdapter = new ViewProductadapter(ViewProductFragment.this, getActivity(), modelListByBrand);
                            gridRecyclerView.setAdapter(listItemAdapter);
                            (brandTextView).setInputType(0);


                            dialog.dismiss();
                            search.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if (s != null)
                                        listItemAdapter.filter(s);

                                }
                            });


                        }
                    }
                });


            }

            @Override
            public void failure(RetrofitError error) {

                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();


                if (error.getKind().equals(RetrofitError.Kind.NETWORK))
                    MkShop.toast(getActivity(), "please check your internet connection");
                else MkShop.toast(getActivity(), error.getMessage());


            }
        });


        return viewGroup;
    }


}
