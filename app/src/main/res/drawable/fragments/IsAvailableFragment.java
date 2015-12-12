package drawable.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobiles.mkshop.R;
import com.mobiles.mkshop.application.MkShop;


public class IsAvailableFragment extends Fragment {



    public static IsAvailableFragment newInstance() {
        IsAvailableFragment fragment = new IsAvailableFragment();

        return fragment;
    }

    public IsAvailableFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MkShop.SCRREN="IsAvailableFragment";
        return inflater.inflate(R.layout.fragment_is_available, container, false);
    }


}
