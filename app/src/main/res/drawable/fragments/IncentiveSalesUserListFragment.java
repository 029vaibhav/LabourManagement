package drawable.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.IncentiveUserlistDialogAdapter;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.enums.PaymentType;
import com.mobiles.mkshop.pojos.models.ExpenseEntity;
import com.mobiles.mkshop.pojos.models.Sales;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class IncentiveSalesUserListFragment extends Fragment {


    static List<Sales> salesList;
    RecyclerView recyclerView;
    EditText amount;
    Button submit;
    MaterialDialog materialDialog;
    String message, id;


    public static IncentiveSalesUserListFragment newInstance(List<Sales> sales, String id, String message) {
        IncentiveSalesUserListFragment fragment = new IncentiveSalesUserListFragment();
        salesList = sales;
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("message", message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        salesList = salesList;
        message = getArguments() != null ? getArguments().getString("message") : "";
        id = getArguments() != null ? getArguments().getString("id") : "";


    }


    public IncentiveSalesUserListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.dialog_incentive_user_sale_list, container, false);
        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.usersalelist);
        amount = (EditText) viewGroup.findViewById(R.id.amount);
        submit = (Button) viewGroup.findViewById(R.id.submit);
        TextView messageTextView = (TextView) viewGroup.findViewById(R.id.message);

        messageTextView.setText(message);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        IncentiveUserlistDialogAdapter incentiveUserlistDialogAdapter = new IncentiveUserlistDialogAdapter(IncentiveSalesUserListFragment.this, salesList);
        recyclerView.setAdapter(incentiveUserlistDialogAdapter);

        materialDialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .cancelable(false)
                .build();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (amount.getText().length() != 0) {

                    materialDialog.show();

                    ExpenseEntity expenseEntity = new ExpenseEntity();
                    expenseEntity.setBrand(salesList.get(0).getBrand());
                    expenseEntity.setModelNo(salesList.get(0).getModelNo());
                    expenseEntity.setPaymentType(PaymentType.Incentive.name());
                    expenseEntity.setUsername(salesList.get(0).getUsername());
                    expenseEntity.setAmount(amount.getText().toString());


                    Client.INSTANCE.payUserIncentive(MkShop.AUTH, expenseEntity, new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {

                            if (materialDialog != null && materialDialog.isShowing())
                            materialDialog.dismiss();

                            MkShop.toast(getActivity(), s);

                            IncentiveUserListFragment fragment = IncentiveUserListFragment.newInstance(message, id);
                            getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();


                            //Fragment fragment = getFragmentManager().findFragmentByTag(IncentiveUserListFragment.TAG);
//                            if (fragment == null) {
//                                fragment = new Incentive();
//                            }
//                            getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();


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
                    });

                } else {
                    MkShop.toast(getActivity(), "please enter the incentive amount");
                }

            }
        });

        return viewGroup;
    }


}
