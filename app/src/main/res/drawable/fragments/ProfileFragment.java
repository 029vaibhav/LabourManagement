package drawable.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.adapters.ProfileAdapter;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.models.Profile;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;


public class ProfileFragment extends Fragment implements ImageChooserListener {


    public static String TAG = "ProfileFragment";
    String username;
    private String mediaPath;
    ImageView header;
    private ImageChooserManager imageChooserManager;
    MaterialDialog materialDialog;


    CollapsingToolbarLayout collapsingToolbar;
    RecyclerView recyclerView;
    int mutedColor = R.attr.colorPrimary;
    ProfileAdapter simpleRecyclerAdapter;
    Bitmap bitmap;

    public static ProfileFragment newInstance(String param1) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("username", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getArguments() != null ? getArguments().getString("username") : "";

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View viewGroup = inflater.inflate(R.layout.fragment_profile, container, false);
        materialDialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .cancelable(false)
                .build();

        collapsingToolbar = (CollapsingToolbarLayout) viewGroup.findViewById(R.id.collapsing_toolbar);

        header = (ImageView) viewGroup.findViewById(R.id.header);


        bitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_mkshop);


        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();

            }
        });


        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.scrollableview);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);


        new GetUserData().execute();


        return viewGroup;
    }

    private void chooseImage() {
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            mediaPath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImageChosen(final ChosenImage image) {


        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (image != null) {

                    materialDialog.show();
                    TypedFile typedFile = new TypedFile("multipart/form-data", new File(image.getFileThumbnail().toString()));


                    Client.INSTANCE.uploadImage(MkShop.AUTH, username, typedFile, "", new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {
                            if (materialDialog != null && materialDialog.isShowing())
                                materialDialog.dismiss();
                            header.setImageURI(Uri.parse(new File(image
                                    .getFileThumbnail()).toString()));
                            MkShop.toast(getActivity(), "uploaded successfully");

                        }

                        @Override
                        public void failure(RetrofitError error) {

                            if (materialDialog != null && materialDialog.isShowing())
                                materialDialog.dismiss();

                            if (error != null) {
                                if (error.getKind().equals(RetrofitError.Kind.NETWORK))
                                    MkShop.toast(getActivity(), "check your internet connection");
                                else MkShop.toast(getActivity(), "something went wrong");

                            }

                        }
                    });


                }
            }
        });


    }

    @Override
    public void onError(final String s) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                MkShop.toast(ProfileFragment.this.getActivity(), s);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("On Activity Result", requestCode + "");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (imageChooserManager == null) {
                imageChooserManager = new ImageChooserManager(this, requestCode, true);
                imageChooserManager.setImageChooserListener(this);
                imageChooserManager.reinitialize(mediaPath);
            }
            imageChooserManager.submit(requestCode, data);
        }
    }


    private class GetUserData extends AsyncTask<Void, Void, Void> {

        MaterialDialog dialog;

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }


        @Override
        protected Void doInBackground(Void... params) {

            Client.INSTANCE.getProfile(MkShop.AUTH, username, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    String json = (new String(((TypedByteArray) response.getBody()).getBytes()));

                    if (materialDialog != null && materialDialog.isShowing())
                        materialDialog.dismiss();

                    json = json.replace(",", "},{").replace(":", ",").replace("{\"", "{\"title\":\"").replace("\",\"", "\",\"value\":\"");

                    Log.e("json", json);


                    Type listType = new TypeToken<List<Profile>>() {
                    }.getType();
                    List<Profile> yourList = new Gson().fromJson(json, listType);

                    String photo = "";
                    for (int i = 0; i < yourList.size(); i++) {
                        if (yourList.get(i).getTitle().equalsIgnoreCase("photo")) {

                            photo = yourList.get(i).getvalue();
                            Log.e("photo", photo);
                            break;
                        }

                    }

                    if (photo.length() > 2)
                        Picasso.with(getActivity()).load(photo.replace("\\", "").replace(",", ":")).into(header);
                    else header.setImageBitmap(bitmap);
//
                    simpleRecyclerAdapter = new ProfileAdapter(username, getActivity(), yourList);
                    recyclerView.setAdapter(simpleRecyclerAdapter);

                }

                @Override
                public void failure(RetrofitError error) {

                    if (materialDialog != null && materialDialog.isShowing())
                        materialDialog.dismiss();

                    if (error.getKind().equals(RetrofitError.Kind.NETWORK))
                        MkShop.toast(getActivity(), "please check your internet connection");
                    else MkShop.toast(getActivity(), error.getMessage());

                }
            });
            return null;
        }
    }
}
