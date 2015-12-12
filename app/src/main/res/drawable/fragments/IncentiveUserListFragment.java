package drawable.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.base.Function;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.IncentiveUserlistAdapter;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.models.Sales;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class IncentiveUserListFragment extends Fragment {

    public static String TAG = "IncentiveUserListFragment";

    String id;
    String message;
    RecyclerView listView;
    MaterialDialog materialDialog;

    public static IncentiveUserListFragment newInstance(String message, String param1) {
        IncentiveUserListFragment fragment = new IncentiveUserListFragment();
        Bundle args = new Bundle();
        args.putString("id", param1);
        args.putString("message", message);
        fragment.setArguments(args);
        return fragment;
    }

    public IncentiveUserListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments() != null ? getArguments().getString("id") : "";
        message = getArguments() != null ? getArguments().getString("message") : "";

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_incentive_user_list, container, false);


        TextView textView = (TextView) viewGroup.findViewById(R.id.message);
        textView.setText(message);
        listView = (RecyclerView) viewGroup.findViewById(R.id.userList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        listView.setLayoutManager(linearLayoutManager);

        materialDialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .cancelable(false)
                .build();
        materialDialog.show();


        Client.INSTANCE.getIncentiveUserList(MkShop.AUTH, id, new Callback<List<Sales>>() {
                    @Override
                    public void success(List<Sales> sales, Response response) {
                        if (materialDialog != null && materialDialog.isShowing())
                        materialDialog.dismiss();

                        ListMultimap<String, Sales> multimap = Multimaps.index(sales, new Function<Sales, String>() {
                            public String apply(Sales source) {
                                return source.getUsername();
                            }

                        });
                        multimap = multimap;
                        IncentiveUserlistAdapter incentiveUserlistAdapter = new IncentiveUserlistAdapter(IncentiveUserListFragment.this, multimap,id,message);
                        listView.setAdapter(incentiveUserlistAdapter);

//                        Log.e("multimap", multimap.toString());


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (materialDialog != null && materialDialog.isShowing())
                        materialDialog.dismiss();

                        if (error.getKind().equals(RetrofitError.Kind.NETWORK))
                            MkShop.toast(getActivity(), "please check your internet connection");

                        else
                            MkShop.toast(getActivity(), error.getMessage());

                    }
                }

        );


        return viewGroup;
    }


}
