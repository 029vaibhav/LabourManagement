package drawable.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.ViewPagerAdapter;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.models.Product;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ViewProductItemFragment extends Fragment {

    String id;
    ViewPager viewPager;
    private SlidingUpPanelLayout mLayout;
    Product p;

    Button buyButton;

    EditText brand, model, sim, screenSize, displayType, platform, iMemory, eMemory, fCamera, rCamera, bluetooth, price, battery;

    public static ViewProductItemFragment newInstance(String param1) {
        ViewProductItemFragment fragment = new ViewProductItemFragment();
        Bundle args = new Bundle();
        args.putString("id", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewProductItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_view_product_item, container, false);
        viewPager = (ViewPager) viewGroup.findViewById(R.id.pager);

        brand = (EditText) viewGroup.findViewById(R.id.brand);
        model = (EditText) viewGroup.findViewById(R.id.model);
        sim = (EditText) viewGroup.findViewById(R.id.sim);
        screenSize = (EditText) viewGroup.findViewById(R.id.screenSize);
        displayType = (EditText) viewGroup.findViewById(R.id.displayType);
        platform = (EditText) viewGroup.findViewById(R.id.platform);
        iMemory = (EditText) viewGroup.findViewById(R.id.iMemory);
        eMemory = (EditText) viewGroup.findViewById(R.id.eMemory);
        fCamera = (EditText) viewGroup.findViewById(R.id.fCamera);
        rCamera = (EditText) viewGroup.findViewById(R.id.rCamera);
        bluetooth = (EditText) viewGroup.findViewById(R.id.bluetooth);
        price = (EditText) viewGroup.findViewById(R.id.price);
        battery = (EditText) viewGroup.findViewById(R.id.battery);

        buyButton = (Button) viewGroup.findViewById(R.id.submit);
        mLayout = (SlidingUpPanelLayout) viewGroup.findViewById(R.id.sliding_layout);


        Client.INSTANCE.getproductid(MkShop.AUTH, id, new Callback<List<Product>>() {
            @Override
            public void success(List<Product> prodcutSales, Response response) {


                Product product = prodcutSales.get(0);
                p = product;
                ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity(), prodcutSales.get(0).getPath());
                viewPager.setAdapter(adapter);

                brand.setText(product.getBrand());
                model.setText(product.getModelNo());
                sim.setText(product.getSim());
                screenSize.setText(product.getScreenSize());
                displayType.setText(product.getDisplayType());
                platform.setText(product.getOs());
                iMemory.setText(product.getiMemory());
                eMemory.setText(product.geteMemory());
                fCamera.setText(product.getfCamera());
                rCamera.setText(product.getbCamera());
                //  bluetooth.setText(product.getBluetooth()+"/"+product.getWlan()+"/"+product.getNfc()+"/"+product.getInfrared()+"/"+product.getRadio());
                price.setText(product.getPrice());
                battery.setText(product.getBattery());


            }

            @Override
            public void failure(RetrofitError error) {

                if (error.getKind().equals(RetrofitError.Kind.NETWORK))
                    MkShop.toast(getActivity(), "Please check your internet connection");
                else
                    MkShop.toast(getActivity(), error.getMessage());

            }
        });

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SaleFragment fragment = SaleFragment.newInstance(p.getBrand(), p.getModelNo());

                getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            }
        });


        return viewGroup;
    }


}
