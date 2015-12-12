package drawable.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobiles.mkshop.R;


public class ExpenseManagerFragment extends Fragment {

    public static String TAG="ExpenseManagerFragment";


    public static ExpenseManagerFragment newInstance(String param1, String param2) {
        ExpenseManagerFragment fragment = new ExpenseManagerFragment();

        return fragment;
    }

    public ExpenseManagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_expense_manager, container, false);

      //  TextView registerProduct = (TextView) viewGroup.findViewById(R.id.registerProduct);
        TextView paySalary = (TextView) viewGroup.findViewById(R.id.paySalary);
        TextView viewExpenses = (TextView) viewGroup.findViewById(R.id.viewExpenses);
        TextView viewBill = (TextView) viewGroup.findViewById(R.id.viewBills);


        paySalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new UserListFragment();
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

            }
        });

        viewExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new ViewExpenseFragment();
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

            }
        });

        viewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new ViewDealersName();
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

            }
        });




        return viewGroup;
    }


}
