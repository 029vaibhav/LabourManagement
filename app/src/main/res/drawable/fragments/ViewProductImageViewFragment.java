package drawable.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobiles.mkshop.R;


public class ViewProductImageViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private String path;


    public static ViewProductImageViewFragment newInstance(String param1) {
        ViewProductImageViewFragment fragment = new ViewProductImageViewFragment();
        Bundle args = new Bundle();
        args.putString("path", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewProductImageViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            path = getArguments().getString("path");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_view_product_image_view, container, false);

        ImageView imageView = (ImageView)viewGroup.findViewById(R.id.mobileImage);

        return  viewGroup;
    }


}
