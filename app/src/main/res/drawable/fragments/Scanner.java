package drawable.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mobiles.mkshop.R;
import com.mobiles.mkshop.interfaces.ScannerCallback;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by vaibhav on 15/11/15.
 */
public class Scanner extends DialogFragment implements ZBarScannerView.ResultHandler, View.OnClickListener {

    ScannerCallback scannerCallback;
    private ZBarScannerView mScannerView;
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private boolean mFlash;
    private boolean mAutoFocus = true;
    private int mCameraId = -1;
    ImageView imageView;


    public void setCallBack(ScannerCallback scannerCallback) {
        this.scannerCallback = scannerCallback;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {


        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.scanner, container, false);

        LinearLayout ll = (LinearLayout) viewGroup.findViewById(R.id.linearlayout);
        imageView = (ImageView) viewGroup.findViewById(R.id.flash);


        mScannerView = new ZBarScannerView(getActivity());
        ll.addView(mScannerView);// Programmatically initialize the scanner view


        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
//            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mCameraId = state.getInt(CAMERA_ID, -1);


        } else {
            mFlash = false;
            mCameraId = -1;

        }
        return viewGroup;
    }


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setHasOptionsMenu(true);
    }


    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(true);


        if (mFlash) {
            imageView.setImageResource(R.drawable.ic_action_flash_on);

        } else {
            imageView.setImageResource(R.drawable.ic_action_flash_off);


        }


        imageView.setOnClickListener(this);


    }


    @Override
    public void handleResult(Result rawResult) {


        scannerCallback.setIMEI(rawResult.getContents());
    }


    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();

    }

    @Override
    public void onClick(View v) {

        if (mFlash) {
            imageView.setImageResource(R.drawable.ic_action_flash_on);
            mScannerView.setFlash(true);
            mFlash = false;

        } else {
            imageView.setImageResource(R.drawable.ic_action_flash_off);
            mScannerView.setFlash(false);
            mFlash = true;

        }

    }


}
