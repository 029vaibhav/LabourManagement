package drawable.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.mobiles.mkshop.R;
import com.mobiles.mkshop.application.Client;
import com.mobiles.mkshop.application.MkShop;
import com.mobiles.mkshop.pojos.models.ProductExpense;

import java.io.ByteArrayOutputStream;
import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class CreateNewTransaction extends Fragment implements ImageChooserListener {
    // TODO: Rename parameter arguments, choose names that match

    public static String TAG = "CreateNewTransaction";

    MaterialDialog materialDialog;
    ImageView header;
    Bitmap bitmap;
    private ImageChooserManager imageChooserManager;
    private String mediaPath;
    EditText dealerNameEditText, totalAmt, note;
    String dealerName;
    Button submit;
    TypedFile typedFile;
    String image;


    public static CreateNewTransaction newInstance(String dealerName) {
        CreateNewTransaction fragment = new CreateNewTransaction();
        Bundle args = new Bundle();
        args.putString("dealerName", dealerName);
        fragment.setArguments(args);
        return fragment;
    }

    public CreateNewTransaction() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dealerName = getArguments() != null ? getArguments().getString("dealerName") : null;
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        MkShop.SCRREN = "CreateNewTransaction";
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_register_product_expense, container, false);

        init(viewGroup);


        if (dealerName != null) {
            dealerNameEditText.setText(dealerName);
            dealerNameEditText.setEnabled(false);
        }

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askImageOption();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOnclick();
            }
        });


        return viewGroup;
    }

    private void submitOnclick() {


        if (dealerNameEditText.getText().length() == 0) {
            MkShop.toast(getActivity(), "please enter dealer name");
        } else if (totalAmt.getText().length() == 0) {
            MkShop.toast(getActivity(), "please enter total amt");
        } else if (image == null) {
            MkShop.toast(getActivity(), "please select bill image");
        } else {
            ProductExpense expenseEntity = new ProductExpense();
            expenseEntity.setTotalAmt(Integer.parseInt(totalAmt.getText().toString()));
            expenseEntity.setDealerName(dealerNameEditText.getText().toString().trim());
            expenseEntity.setNote("" + note.getText());
            expenseEntity.setImage(image);

            materialDialog.show();
            Client.INSTANCE.productPurchase(MkShop.AUTH, expenseEntity, new Callback<String>() {
                @Override
                public void success(String s, Response response) {

                    if (materialDialog != null && materialDialog.isShowing())
                        materialDialog.dismiss();
                    MkShop.toast(getActivity(), s);
                }

                @Override
                public void failure(RetrofitError error) {

                    if (materialDialog != null && materialDialog.isShowing())
                        materialDialog.dismiss();
                    MkShop.toast(getActivity(), error.getMessage());
                }
            });
        }
    }

    private void init(ViewGroup viewGroup) {


        materialDialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .cancelable(false)
                .build();

        dealerNameEditText = (EditText) viewGroup.findViewById(R.id.dealer_edit);
        totalAmt = (EditText) viewGroup.findViewById(R.id.total_edit);
        note = (EditText) viewGroup.findViewById(R.id.note);
        submit = (Button) viewGroup.findViewById(R.id.submit);
        header = (ImageView) viewGroup.findViewById(R.id.header);


        bitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_mkshop);
    }

    private void askImageOption() {

        new MaterialDialog.Builder(getActivity())
                .items(R.array.image_option)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {

                        switch (which) {
                            case 0: //view profile
                                if (dialog != null && dialog.isShowing())
                                    dialog.dismiss();
                                takePicture();
                                break;
                            case 1: // view attendance
                                if (dialog != null && dialog.isShowing())
                                    dialog.dismiss();
                                chooseImage();
                                break;

                        }


                    }
                })
                .show();


    }

    private void takePicture() {
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            mediaPath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void onImageChosen(final ChosenImage chosenImage) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {


                if (chosenImage != null) {
                    typedFile = new TypedFile("multipart/form-data", new File(chosenImage.getFileThumbnail().toString()));
                    header.setImageURI(Uri.parse(new File(chosenImage
                            .getFileThumbnail()).toString()));

                    Bitmap bitmap = ((BitmapDrawable) header.getDrawable()).getBitmap();

                    image = Base64.encodeToString(getBytesFromBitmap(bitmap),
                            Base64.NO_WRAP);


//                    image = chosenImage.getFileThumbnail().getBytes();
                }
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

    @Override
    public void onError(String s) {

    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

}
