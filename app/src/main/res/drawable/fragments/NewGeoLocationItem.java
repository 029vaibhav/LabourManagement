package drawable.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.models.Location;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class NewGeoLocationItem extends Fragment
        implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener

{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    MaterialDialog materialDialog;

    public static String TAG = "NewGeoLocationItem";
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    private static final String lat = "lat";
    private static final String longi = "longi";
    private static final String prole = "role";
//    private static final String pradius = "radius";
    private static final String id = "id";

    android.location.Location location;

    String[] roleOption = {"Salesman", "Technician", "Receptionist"};
    private String mlat = "lat";
    private String mlongi = "longi";
    private String mrole = "role";
//    private String mradius = "radius";
    private String mid = null;

    EditText latitude, longitude;
//    radius;
    AutoCompleteTextView role;
    Button submit, currentLocation;

    public static NewGeoLocationItem newInstance(String param1, String param2, String param3, String param4, String param5) {
        NewGeoLocationItem fragment = new NewGeoLocationItem();
        Bundle args = new Bundle();
        args.putString(lat, param1);
        args.putString(longi, param2);
        args.putString(prole, param3);
//        args.putString(pradius, param4);
        args.putString(id, param5);
        fragment.setArguments(args);
        return fragment;
    }

    public NewGeoLocationItem() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mlat = getArguments().getString(lat);
            mlongi = getArguments().getString(longi);
            mrole = getArguments().getString(prole);
//            mradius = getArguments().getString(pradius);
            mid = getArguments().getString(id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.geo_location_list_item, container, false);

        latitude = (EditText) viewGroup.findViewById(R.id.latitude);
        longitude = (EditText) viewGroup.findViewById(R.id.longitude);
        role = (AutoCompleteTextView) viewGroup.findViewById(R.id.role);
//        radius = (EditText) viewGroup.findViewById(R.id.radius);
        submit = (Button) viewGroup.findViewById(R.id.submit);
        currentLocation = (Button) viewGroup.findViewById(R.id.currentLocation);
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsAlert();
            }
        });

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.select_dialog_item, roleOption);
        role.setThreshold(1);
        role.setAdapter(adapter);

        if (getArguments() != null) {
            latitude.setText(mlat);
            longitude.setText(mlongi);
            role.setText(mrole);
//            radius.setText(mradius);
        }
        materialDialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .cancelable(false)
                .build();

        submit.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


                if (latitude.getText().length() == 0 || longitude.getText().length() == 0)
                    MkShop.toast(getActivity(), "please enter both the wifi or enter Na");
                else if (role.getText().length() == 0)
                    MkShop.toast(getActivity(), "please enter role");
//                else if (radius.getText().length() == 0)
//                    MkShop.toast(getActivity(), "please enter radius");
                else {
                    materialDialog.show();
                    Location location = new Location();
                    if (mid != null) location.setId(Integer.parseInt(mid));
                    location.setLatitude(latitude.getText().toString());
                    location.setLongitude(longitude.getText().toString());
//                    location.setRadius(radius.getText().toString());
                    location.setRole(role.getText().toString());
                    Client.INSTANCE.setLocation(MkShop.AUTH, location, new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {

                            if (materialDialog != null && materialDialog.isShowing())
                                materialDialog.dismiss();
                            MkShop.toast(getActivity(), s);
                            Fragment fragment = new GeoPointsFragment();
                            getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if (materialDialog != null && materialDialog.isShowing())
                                materialDialog.dismiss();
                            if (error.getKind().equals(RetrofitError.Kind.NETWORK))
                                MkShop.toast(getActivity(), "check your internet connection");
                            else MkShop.toast(getActivity(), error.getMessage());


                        }
                    });

                }


            }
        });

        return viewGroup;
    }


    private void showSettingsAlert() {
        // TODO Auto-generated method stub


        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.


        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks((this))
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);


        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(LocationSettingsResult result) {
                // TODO Auto-generated method stub
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            Log.e("RESOLUTION_REQUIRED", "REQUEST_CHECK_SETTINGS");
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("dfd", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("fdfd", "Location settings are inadequate, and cannot be fixed here. Dialog " +
                                "not created.");
                        break;
                }
            }
        });


    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(android.location.Location locationc) {

        location = locationc;
        updateUi();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void startLocationUpdates() {

        materialDialog.show();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient,
                locationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                updateUi();
            }
        });

    }

    private void updateUi() {


        if (materialDialog != null && materialDialog.isShowing())
            materialDialog.dismiss();
        if (location != null) {
            latitude.setText("" + location.getLatitude());
            longitude.setText("" + location.getLongitude());
        } else {
            MkShop.toast(getActivity(), "Please try after 10 secs while we fetch your location");
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("RESULT_CANCELED", "User chose not to make required location settings changes.");
                        MkShop.toast(getActivity(), "please turn on the gps to fetch location");

                        break;
                }
                break;
        }
    }

}
