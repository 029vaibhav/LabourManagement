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
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.ViewBillAdapter;
import com.mobiles.mkshop.pojos.models.ProductExpense;

import java.util.List;


public class ViewBills extends Fragment {

    TextView toDate, fromDate, totalQuantity, totalRevenue;
    String sFromdate, sToDate;
    MaterialDialog materialDialog;
    RecyclerView recyclerView;

    String dealerName;

    public static ViewBills newInstance(String dealerName) {
        ViewBills fragment = new ViewBills();
        Bundle args = new Bundle();
        args.putString("dealerName", dealerName);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewBills() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dealerName = getArguments() != null ? getArguments().getString("dealerName") : "";

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_view_bills, container, false);

        fromDate = (TextView) viewGroup.findViewById(R.id.fromDate);
        toDate = (TextView) viewGroup.findViewById(R.id.toDate);

        totalQuantity = (TextView) viewGroup.findViewById(R.id.totalQuantity);
        totalRevenue = (TextView) viewGroup.findViewById(R.id.totalRevenue);

        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);


        materialDialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .cancelable(false)
                .build();

        executeQuery();

        return viewGroup;
    }

    private void executeQuery() {

        materialDialog.show();

        List<ProductExpense> expenses = ProductExpense.find(ProductExpense.class, "dealer_name = ?", dealerName);

        ViewBillAdapter viewBillAdapter = new ViewBillAdapter(ViewBills.this, expenses);
        recyclerView.setAdapter(viewBillAdapter);

        materialDialog.dismiss();

//        Client.INSTANCE.getPurchasedProduct(MkShop.AUTH, sFromdate, sToDate, new Callback<List<ProductExpense>>() {
//            @Override
//            public void success(final List<ProductExpense> productExpenses, Response response) {
//
//                if (materialDialog != null && materialDialog.isShowing())
//                    materialDialog.dismiss();
//
//
//
//                for (int i=0;i<productExpenses.size();i++)
//                {
//                   List<ProductExpense> expenses= ProductExpense.find(ProductExpense.class, "server_id = ?", productExpenses.get(i).getServerId());
//                    if(expenses!=null)
//                    {
//                        expenses.get(0).save();
//                    }
//                }
//
//                List<ProductExpense> productExpense = ProductExpense.listAll(ProductExpense.class);
//
//                ViewBillAdapter viewBillAdapter = new ViewBillAdapter(ViewBills.this, productExpense);
//                recyclerView.setAdapter(viewBillAdapter);
//
//
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (materialDialog != null && materialDialog.isShowing())
//                    materialDialog.dismiss();
//                MkShop.toast(getActivity(), error.getMessage().toString());
//
//            }
//        });


    }


}
