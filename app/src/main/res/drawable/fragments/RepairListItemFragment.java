package drawable.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.application.Myenum;
import com.mobiles.mkshop.pojos.models.ServiceCenterEntity;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class RepairListItemFragment extends Fragment {


    public static String TAG = "serviceItem";
    private TextView brand, modelNo, status;
    EditText price, other, jobNo, problem, resolution;
    Button submit;
    String stringStatus;
    int index;
    int id;
    ServiceCenterEntity service;


    public static RepairListItemFragment newInstance() {
        RepairListItemFragment fragment = new RepairListItemFragment();
        return fragment;
    }

    public RepairListItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        MkShop.SCRREN = "RepairListItemFragment";

        service = Myenum.INSTANCE.getServiceCenterEntity();

        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_repair_list_item, container, false);
        initView(v);


        problem.setText(service.getProblem());
        brand.setText(service.getBrand().replace("\\n", ""));
        modelNo.setText(service.getModel());
        price.setText("" + service.getPrice());
        jobNo.setText(service.getJobNo());
        jobNo.setEnabled(false);

        if (service.getResolution() != null && !service.getResolution().equalsIgnoreCase("null"))
            resolution.setText(service.getResolution());

        status.setText(service.getStatus());
        stringStatus = service.getStatus();
        setIndex(service.getStatus());


        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .items(R.array.items)
                        .itemsCallbackSingleChoice(index, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                stringStatus = text.toString();

                                if (stringStatus.equalsIgnoreCase("Returned")) {
                                    price.setVisibility(View.GONE);
                                    price.setText("0");
                                } else {
                                    price.setVisibility(View.VISIBLE);
                                }
                                status.setText(stringStatus);
                                setIndex(stringStatus);
                                return true;
                            }
                        })
                        .positiveText("select")
                        .show();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                service.setPrice(price.getText().toString());
                service.setStatus(stringStatus);
                service.setProblem(problem.getText().toString());
                service.setDeliveryDate("");
                service.setUsername(MkShop.Username);
                if (resolution.getText().length() > 0)
                    service.setResolution(resolution.getText().toString());
                else service.setResolution("");

                new SendData().execute();
            }
        });


        return v;
    }

    private void initView(ViewGroup v) {
        brand = (TextView) v.findViewById(R.id.brandtext);
        status = (TextView) v.findViewById(R.id.status);
        modelNo = (TextView) v.findViewById(R.id.modeltext);
        price = (EditText) v.findViewById(R.id.priceEdit);
        other = (EditText) v.findViewById(R.id.otheredit);
        jobNo = (EditText) v.findViewById(R.id.jobnoedit);
        problem = (EditText) v.findViewById(R.id.problemedit);
        submit = (Button) v.findViewById(R.id.submit);
        resolution = (EditText) v.findViewById(R.id.resolutionEdit);

    }

    private int setIndex(String status) {
        if (status.equalsIgnoreCase("Pending")) {
            index = 0;
        } else if (status.equalsIgnoreCase("Processing")) {
            index = 1;
        } else if (status.equalsIgnoreCase("Pna")) {

            index = 2;
        } else if (status.equalsIgnoreCase("Done")) {
            index = 3;
        } else if (status.equalsIgnoreCase("Return")) {
            index = 4;
        } else if (status.equalsIgnoreCase("Delivered")) {
            index = 5;
        } else if (status.equalsIgnoreCase("Returned")) {
            index = 6;
        }
        return index;

    }

    private class SendData extends AsyncTask<Void, Void, Void> {

        MaterialDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new MaterialDialog.Builder(getActivity())
                    .content("please wait")
                    .progress(true, 0)
                    .build();
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(Void... params) {


            Client.INSTANCE.sendService(MkShop.AUTH, service, new Callback<String>() {
                @Override
                public void success(String s, Response response) {


                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();

                    MkShop.toast(getActivity(), s);
                    Fragment fragment = new RequestRepair();

                    getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
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
