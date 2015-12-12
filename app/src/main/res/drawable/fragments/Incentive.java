package drawable.fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.IncentiveAdapter;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.models.IncentiveEntity;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class Incentive extends Fragment {

    public static String TAG = "Attendance";
    RecyclerView recyclerView;
    MaterialDialog materialDialog;


    public static Incentive newInstance(int pos) {
        Incentive fragment = new Incentive();


        return fragment;
    }

    public Incentive() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_incentive, container, false);
        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.incentiveList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        FloatingActionButton fab = (FloatingActionButton) viewGroup.findViewById(R.id.fab);
        //   fab.attachToRecyclerView(recyclerView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new NewIncentiveFragment();
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
            }
        });

        materialDialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .cancelable(false)
                .build();

        materialDialog.show();

        Client.INSTANCE.getIncentiveList(MkShop.AUTH, new Callback<List<IncentiveEntity>>() {
            @Override
            public void success(List<IncentiveEntity> incentiveEntity, Response response) {

                if (materialDialog != null && materialDialog.isShowing())
                    materialDialog.dismiss();
                IncentiveAdapter incentiveAdapter = new IncentiveAdapter(Incentive.this, incentiveEntity);
                recyclerView.setAdapter(incentiveAdapter);


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

        return viewGroup;
    }


}
