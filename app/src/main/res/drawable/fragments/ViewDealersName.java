package drawable.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.mobiles.mkshop.adapters.ViewDealerNameAdapter;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.models.PaymentHistory;
import com.mobiles.mkshop.pojos.models.ProductExpense;
import com.mobiles.mkshop.pojos.models.PurchaseHistory;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ViewDealersName extends Fragment implements View.OnClickListener {

    String sFromDate, sToDate;
    MaterialDialog materialDialog;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    FloatingActionButton fab;

    public static ViewDealersName newInstance() {
        ViewDealersName fragment = new ViewDealersName();

        return fragment;
    }

    public ViewDealersName() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.dealer_name, container, false);


        init(viewGroup);


        fab.setOnClickListener(this);

        executeQuery();

        return viewGroup;
    }

    private void init(ViewGroup viewGroup) {


        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.recycler_view);
        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.recycler_view);
        fab = (FloatingActionButton) viewGroup.findViewById(R.id.fab);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);


        materialDialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .cancelable(false)
                .build();


        sharedPreferences = getActivity().getSharedPreferences("MKSHOP", Context.MODE_PRIVATE);

        sFromDate = sharedPreferences.getString(MkShop.LAST_VIEWED_DATE, "2015-01-01");
        sToDate = DateTime.now().toString("yyyy-MM-dd");

    }

    private void executeQuery() {

        materialDialog.show();

        Client.INSTANCE.getPurchasedProduct(MkShop.AUTH, sFromDate, sToDate, new Callback<List<ProductExpense>>() {
            @Override
            public void success(final List<ProductExpense> serverProductExpenses, Response response) {

                sharedPreferences.edit().putString(MkShop.LAST_VIEWED_DATE, sToDate).commit();
                if (materialDialog != null && materialDialog.isShowing())
                    materialDialog.dismiss();
                for (int i = 0; i < serverProductExpenses.size(); i++) {
                    List<ProductExpense> expenses = ProductExpense.find(ProductExpense.class, "dealer_name = ?", serverProductExpenses.get(i).getDealerName());
                    if (expenses == null || expenses.size() == 0) {
                        serverProductExpenses.get(i).save();
                    }
                    serverProductExpenses.get(i).setEntries(serverProductExpenses.get(i).getPaymentHistories());
                    serverProductExpenses.get(i).setPurchaseEntries(serverProductExpenses.get(i).getPurchaseHistory());
                }
                List<ProductExpense> productExpense = ProductExpense.listAll(ProductExpense.class);

                Map<String, PurchaseHistory> map = new HashMap<>();


                for (ProductExpense productExpense1 : productExpense) {

                    String dealerName = productExpense1.getDealerName();

                    List<PurchaseHistory> purchaseHistories = PurchaseHistory.find(PurchaseHistory.class, "dealer_name = ?", dealerName);

                    int totalAmount = 0;
                    for (PurchaseHistory purchaseHistory : purchaseHistories) {
                        totalAmount += Integer.parseInt(purchaseHistory.getTotalAmt());
                    }


                    List<PaymentHistory> paymentHistories = PaymentHistory.find(PaymentHistory.class, "dealer_id =?", dealerName);
                    int dueAmount = 0;
                    for (PaymentHistory paymentHistory : paymentHistories) {
                        dueAmount += Integer.parseInt(paymentHistory.getAmount());
                    }

                    productExpense1.setTotalAmt(totalAmount);
                    productExpense1.setDueAmount(totalAmount - dueAmount);
                    productExpense1.save();


                }


                ViewDealerNameAdapter viewBillAdapter = new ViewDealerNameAdapter(ViewDealersName.this, productExpense);
                recyclerView.setAdapter(viewBillAdapter);

            }

            @Override
            public void failure(RetrofitError error) {

                if (materialDialog != null && materialDialog.isShowing())
                    materialDialog.dismiss();

                if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                    MkShop.toast(getActivity(), "please check your internet connection");
                } else
                    MkShop.toast(getActivity(), error.getMessage().toString());

            }
        });


    }


    @Override
    public void onClick(View v) {

        CreateNewTransaction fragment = CreateNewTransaction.newInstance(null);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }
}
