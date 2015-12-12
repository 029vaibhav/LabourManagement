package drawable.fragments;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.application.Myenum;
import com.mobiles.mkshop.pojos.enums.Status;
import com.mobiles.mkshop.pojos.models.PartsRequests;

import org.joda.time.DateTime;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class PartsRequestListItemFragment extends Fragment {


    public static String TAG = "PartsRequestListItemFragment";
    private TextView date, status,dateTitle;
    EditText price, customerName, part, mobileNo;
    Button submit;
    String Stringdate, stringStatus;
    int index;
    PartsRequests partsRequests;


    public static PartsRequestListItemFragment newInstance() {
        PartsRequestListItemFragment fragment = new PartsRequestListItemFragment();
        return fragment;
    }

    public PartsRequestListItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        MkShop.SCRREN = "PartsRequestListItemFragment";


        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.request_part_new_item, container, false);

        partsRequests = Myenum.INSTANCE.getRequestRepair();

        customerName = (EditText) v.findViewById(R.id.customername);
        customerName.setEnabled(false);
        status = (TextView) v.findViewById(R.id.status);
        date = (TextView) v.findViewById(R.id.datetext);
        dateTitle = (TextView) v.findViewById(R.id.date_title);
        price = (EditText) v.findViewById(R.id.priceEdit);
        price.setEnabled(false);
        mobileNo = (EditText) v.findViewById(R.id.mobile);
        mobileNo.setEnabled(false);
        part = (EditText) v.findViewById(R.id.partsrequired);
        part.setEnabled(false);
        submit = (Button) v.findViewById(R.id.submit);

        status.setVisibility(View.VISIBLE);
        customerName.setText(partsRequests.getCustomerName());
        status.setText(partsRequests.getStatus());


        if(!partsRequests.getStatus().equalsIgnoreCase(Status.PENDING.name()))
        {
            date.setVisibility(View.VISIBLE);
            dateTitle.setVisibility(View.VISIBLE);
        }

        date.setText(partsRequests.getDeliveryDate());

        price.setText("" + partsRequests.getPrice());

        mobileNo.setText("" + partsRequests.getMobileNo());
        part.setText(partsRequests.getPart());


        setindex(partsRequests.getStatus());
        stringStatus =partsRequests.getStatus();


        date.setOnClickListener(new View.OnClickListener() {

            DatePickerDialog datePickerDialog;

            @Override
            public void onClick(View v) {

                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        Stringdate = "" + year + "-" + monthOfYear + "-" + dayOfMonth;
                        date.setText("" + dayOfMonth + "-" + monthOfYear+"-"+year);
                        datePickerDialog.dismiss();

                    }
                }, DateTime.now().getYear(), DateTime.now().getMonthOfYear(), DateTime.now().getDayOfMonth());
                datePickerDialog.show();
            }
        });


        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .items(R.array.requestPartStatus)
                        .itemsCallbackSingleChoice(index, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                if (text != null) {
                                    stringStatus = text.toString();
                                    if (!stringStatus.equalsIgnoreCase("Pending")) {
                                        date.setVisibility(View.VISIBLE);
                                        dateTitle.setVisibility(View.VISIBLE);
                                    } else {
                                        date.setVisibility(View.GONE);
                                        dateTitle.setVisibility(View.GONE);
                                    }
                                    status.setText(stringStatus);
                                    setindex(stringStatus);
                                }
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
                if (customerName.getText().toString().length() <= 0) {
                    MkShop.toast(getActivity(), "please enter customer name");

                } else if (mobileNo.getText().length() < 10 || mobileNo.getText().length() > 10) {
                    MkShop.toast(getActivity(), "check mobile no");

                } else if (!stringStatus.equalsIgnoreCase(Status.PENDING.name()) && date.getText().toString().equalsIgnoreCase("date") || date.getText().toString().length() <= 0) {
                    MkShop.toast(getActivity(), "please select date");

                } else if (price.getText().length() <= 0) {
                    MkShop.toast(getActivity(), "please enter price");

                } else if (part.getText().length() <= 0) {
                    MkShop.toast(getActivity(), "please enter part required");

                } else {


                    partsRequests.setStatus(stringStatus);

                    if(!date.getText().toString().equalsIgnoreCase("date") || date.getText().toString().length() > 0)
                    partsRequests.setDeliveryDate(Stringdate);

                    Client.INSTANCE.sendPartRequest(MkShop.AUTH,partsRequests, new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {

                            Toast.makeText(getActivity(), "success " + s, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });                }
            }
        });


        return v;
    }

    private class SendData extends AsyncTask<Void, Void, Void> {

        MaterialDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new MaterialDialog.Builder(getActivity())
                    .content("please wait")
                    .progress(true, 0)
                    .show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {


            return null;
        }
    }

    private int setindex(String status) {


        if (status.equalsIgnoreCase("Pending")) {
            index = 0;
        } else if (status.equalsIgnoreCase("Recieved")) {
            index = 1;
        } else if (status.equalsIgnoreCase("Delivered")) {
            index = 2;
        }
        return index;

    }
}
