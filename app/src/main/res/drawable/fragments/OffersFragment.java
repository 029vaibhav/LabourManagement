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
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.NotificationAdapter;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.models.Notification;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OffersFragment extends Fragment {

    public static String TAG = "OffersFragment";


    RecyclerView recyclerView;
    EditText search;
    NotificationAdapter listItemAdapter;
    MaterialDialog materialDialog;


    public static OffersFragment newInstance(String param1, String param2) {
        OffersFragment fragment = new OffersFragment();
        return fragment;
    }

    public OffersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_offers, container, false);
        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.notificationList);
        search = (EditText) viewGroup.findViewById(R.id.edit_search);

        materialDialog = new MaterialDialog.Builder(getActivity())
                .progress(false, 0)
                .content("please wait")
                .cancelable(false)
                .build();


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && listItemAdapter != null)
                    listItemAdapter.filter(s);

            }
        });


        materialDialog.show();
        Client.INSTANCE.getNotificationDetail(MkShop.AUTH, MkShop.Role, new Callback<List<Notification>>() {
            @Override
            public void success(List<Notification> notifications, Response response) {

                if (materialDialog != null && materialDialog.isShowing())
                    materialDialog.dismiss();
                listItemAdapter = new NotificationAdapter(getActivity(), notifications);
                recyclerView.setAdapter(listItemAdapter);


            }

            @Override
            public void failure(RetrofitError error) {

                if (materialDialog != null && materialDialog.isShowing())
                    materialDialog.dismiss();


                if (error.getKind().equals(RetrofitError.Kind.NETWORK))
                    MkShop.toast(getActivity(), "check your internet connection");
                else
                    MkShop.toast(getActivity(), error.getMessage());

            }
        });


        return viewGroup;

    }


}
