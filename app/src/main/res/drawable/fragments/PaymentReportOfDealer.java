package drawable.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.DealerReportItemAdapter;
import com.mobiles.mkshop.adapters.TabsPagerDealerAdapter;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.application.Myenum;
import com.mobiles.mkshop.pojos.models.PaymentHistory;
import com.mobiles.mkshop.pojos.models.PurchaseHistory;

import java.util.List;

public class PaymentReportOfDealer extends Fragment implements TextWatcher {

    private FragmentActivity myContext;
    public static String TAG = "PaymentReportOfDealer";
    TextView totalQuantity, totalRevenue;
    ViewPager viewPager;
    TabsPagerDealerAdapter adapter;
    EditText search;


    String dealerName;

    public static PaymentReportOfDealer newInstance(String dealerName) {
        PaymentReportOfDealer fragment = new PaymentReportOfDealer();
        Bundle args = new Bundle();
        args.putString("dealerName", dealerName);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dealerName = getArguments() != null ? getArguments().getString("dealerName") : "";

    }

    public PaymentReportOfDealer() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        myContext = (FragmentActivity) context;
        super.onAttach(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MkShop.SCRREN = "SalesReport";

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.dealer_expense_report, container, false);

        init(viewGroup);

        initList();

        search.addTextChangedListener(this);


        return viewGroup;
    }

    private void initList() {

        List<PurchaseHistory> purchaseHistories = PurchaseHistory.find(PurchaseHistory.class, "dealer_name = ?", dealerName);
        List<PaymentHistory> paymentHistories = PaymentHistory.find(PaymentHistory.class, "dealer_id =?", dealerName);


        Myenum.INSTANCE.setPurchaseHistories(purchaseHistories);
        Myenum.INSTANCE.setPaymentHistories(paymentHistories);

    }

    private void init(ViewGroup viewGroup) {

        viewPager = (ViewPager) viewGroup.findViewById(R.id.pager);

        totalQuantity = (TextView) viewGroup.findViewById(R.id.totalQuantity);
        totalRevenue = (TextView) viewGroup.findViewById(R.id.totalRevenue);
        search = (EditText) viewGroup.findViewById(R.id.edit_search);


        adapter = new TabsPagerDealerAdapter(myContext.getSupportFragmentManager());
        adapter.setDealerName(dealerName);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) viewGroup.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);


    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        int index = viewPager.getCurrentItem();
        TabsPagerDealerAdapter adapter = ((TabsPagerDealerAdapter) viewPager.getAdapter());
        DealerReportViewPagerFragment page = adapter.getFragment(index);
        RecyclerView listView = (RecyclerView) page.getView().findViewById(R.id.recycler_view);
        DealerReportItemAdapter adapters = (DealerReportItemAdapter) listView.getAdapter();
        adapters.setSearchText(s);


    }
}
