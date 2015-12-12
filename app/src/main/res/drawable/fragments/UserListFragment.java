package drawable.fragments;

import android.os.AsyncTask;
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
import com.mobiles.mkshop.adapters.UserListItemAdpater;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.models.UserListAttendance;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vaibhav on 4/7/15.
 */
public class UserListFragment extends Fragment {


    public static String TAG = "UserListFragment";

    UserListItemAdpater userListItemAdpater;
    List<UserListAttendance> userListAttendances;
    RecyclerView recyclerView;


    public static UserListFragment newInstance() {
        UserListFragment fragment = new UserListFragment();
        return fragment;
    }

    public UserListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MkShop.SCRREN = "UserListFragment";
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_user_list, container, false);


        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.repairlist);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        FloatingActionButton fab = (FloatingActionButton) viewGroup.findViewById(R.id.fab);
        // fab.attachToRecyclerView(recyclerView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new NewProfileFragment();
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
            }
        });


        new GetUserLsit().execute();


        return viewGroup;


    }

    private class GetUserLsit extends AsyncTask<Void, Void, Void> {

        MaterialDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new MaterialDialog.Builder(getActivity())
                    .content("please wait")
                    .progress(true, 0)
                    .show();
        }


        @Override
        protected Void doInBackground(Void... params) {

            Client.INSTANCE.getUserList(MkShop.AUTH, new Callback<List<UserListAttendance>>() {
                @Override
                public void success(List<UserListAttendance> userListAttendances, Response response) {

                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                    userListItemAdpater = new UserListItemAdpater(UserListFragment.this, userListAttendances);
                    recyclerView.setAdapter(userListItemAdpater);
                }

                @Override
                public void failure(RetrofitError error) {

                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();

                    if (error.getKind().equals(RetrofitError.Kind.NETWORK))
                        MkShop.toast(getActivity(), "please check your internet connection");
                    else
                        MkShop.toast(getActivity(), error.getMessage());

                }
            });

            return null;
        }
    }
}
