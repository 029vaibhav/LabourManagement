package drawable.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.models.Notification;

import org.joda.time.DateTime;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SendNotificationFragment extends Fragment {

    MaterialDialog dialog;


    public static String TAG = "SendNotificationFragment";
    String validityDate;
    String[] roleOption = {"Admin", "Salesman", "Technician", "Receptionist"};


    public static SendNotificationFragment newInstance(String param1, String param2) {
        SendNotificationFragment fragment = new SendNotificationFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    public SendNotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_send_notification, container, false);

        dialog = new MaterialDialog.Builder(getActivity())
                .content("please wait")
                .progress(true, 0).build();
        final EditText message = (EditText) viewGroup.findViewById(R.id.message);
        final TextView date = (TextView) viewGroup.findViewById(R.id.validity);
        final AutoCompleteTextView role = (AutoCompleteTextView) viewGroup.findViewById(R.id.role);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.select_dialog_item, roleOption);
        role.setThreshold(1);
        role.setAdapter(adapter);

        Button submit = (Button) viewGroup.findViewById(R.id.submit);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {


                        String sToDate = MkShop.checkDigit(i3) + "-" + MkShop.checkDigit(i2 + 1) + "-" + i;


                        date.setText(sToDate);
                        DateTime dt = new DateTime(i, i2 + 1, i3, 01, 01);

                        validityDate = dt.toString("yyyy-MM-dd");


                    }
                }, DateTime.now().getYear(), DateTime.now().getMonthOfYear() - 1, DateTime.now().getDayOfMonth());
                datePickerDialog.show();

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (message.getText().length() == 0)
                    MkShop.toast(getActivity(), "please provide message");
                else if (role.getText().length() == 0)
                    MkShop.toast(getActivity(), "please specify role");
                else if (date.getText().toString().equalsIgnoreCase("Validity"))
                    MkShop.toast(getActivity(), "please select validity date");
                else {

                    dialog.show();

                    Notification notification = new Notification();
                    notification.setRole(role.getText().toString());
                    notification.setEndDate(validityDate);

                    notification.setMessage(message.getText().toString());
                    Client.INSTANCE.sendNotification(MkShop.AUTH, notification, new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {

                            if (dialog != null && dialog.isShowing())
                                dialog.dismiss();


                            MkShop.toast(getActivity(), s);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if (dialog != null && dialog.isShowing())
                                dialog.dismiss();

                            if (error.getKind().equals(RetrofitError.Kind.NETWORK))
                                MkShop.toast(getActivity(), "please check your internet connection");

                        }
                    });
                }


            }
        });


        return viewGroup;
    }



}
