package drawable.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.DealerReportItemAdapter;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.enums.TransactionType;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class DealerReportViewPagerFragment extends Fragment implements View.OnClickListener {



    public static String TAG = "DealerReportViewPagerFragment";

    TransactionType transactionType;
    int pt;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    DealerReportItemAdapter listItemAdapter;
    String dealerName;
    Dialog dialog;
    MaterialDialog progressMaterialDialog;
    EditText dealerNameEditText, note, amount;
    Button submit;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pt = getArguments() != null ? getArguments().getInt("num") : 1;

        if (pt == 0) {
            transactionType = TransactionType.Purchase;
        } else {
            transactionType = TransactionType.Payment;
        }

        dealerName = getArguments() != null ? getArguments().getString("dealerName") : "";


    }

    public static DealerReportViewPagerFragment newInstance(int pos, String dealerName) {
        DealerReportViewPagerFragment fragment = new DealerReportViewPagerFragment();
        Bundle args = new Bundle();
        args.putInt("num", pos);
        args.putString("dealerName", dealerName);
        fragment.setArguments(args);

        return fragment;
    }

    public DealerReportViewPagerFragment() {
        // Required empty public constructor
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.dealer_name, container, false);

        init(viewGroup);

        recyclerView.setLayoutManager(linearLayoutManager);
        listItemAdapter = new DealerReportItemAdapter(this, transactionType);
        recyclerView.setAdapter(listItemAdapter);


        fab.setOnClickListener(this);


        return viewGroup;
    }

    private void init(ViewGroup viewGroup) {

        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.recycler_view);
        fab = (FloatingActionButton) viewGroup.findViewById(R.id.fab);

        linearLayoutManager = new LinearLayoutManager(getActivity());


        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dealer_pay_due_amount);


        dealerNameEditText = (EditText) dialog.findViewById(R.id.dealer_edit);
        dealerNameEditText.setEnabled(false);
        amount = (EditText) dialog.findViewById(R.id.total_edit);
        note = (EditText) dialog.findViewById(R.id.note);
        submit = (Button) dialog.findViewById(R.id.submit);


        progressMaterialDialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .cancelable(false)
                .build();

    }


    @Override
    public void onClick(View v) {

        if (transactionType == TransactionType.Purchase) {
            CreateNewTransaction fragment = CreateNewTransaction.newInstance(dealerName);
            getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        } else {

            dialog.show();
            dealerNameEditText.setText(dealerName);
            dialogSubmitRules();

        }

    }

    private void dialogSubmitRules() {

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (amount.getText().length() == 0) {
                    MkShop.toast(getActivity(), "amount cant be blank");
                } else if (note.getText().length() == 0) {
                    MkShop.toast(getActivity(), "note cant be blank");
                } else {
                    sendDataToServer(dealerName, amount.getText().toString(), note.getText().toString());

                }
            }
        });
    }

    private void sendDataToServer(String dealerName, String amount, String note) {

        progressMaterialDialog.show();
        Client.INSTANCE.duePayment(MkShop.AUTH, dealerName, amount, note, new Callback<String>() {
            @Override
            public void success(String s, Response response) {

                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();

                if (progressMaterialDialog != null && progressMaterialDialog.isShowing())
                    progressMaterialDialog.dismiss();
                MkShop.toast(getActivity(), s);

                Fragment fragment = new ViewDealersName();
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();


            }

            @Override
            public void failure(RetrofitError error) {
                if (progressMaterialDialog != null && progressMaterialDialog.isShowing())
                    progressMaterialDialog.dismiss();
                if (error.getKind().equals(RetrofitError.Kind.NETWORK))
                    MkShop.toast(getActivity(), "please check your internet connection");


            }
        });
    }
}
