package com.mobiweb.expense.manager.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.vaibhav.labour.management.R;

/**
 * Created by vaibhav on 7/12/15.
 */
public class Global {


    Context context;
    DB snappyDB;
    ProgressDialog progressDialog;

    public Global(Context context, String currentFragment) {
        this.context = context;
        Constant.currentFragment = currentFragment;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("please wait");


    }

    public void toast(String message) {
        if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public DB openDB() {
        try {
            snappyDB = DBFactory.open(context, context.getString(R.string.db));
            return snappyDB;
        } catch (SnappydbException e) {

        }
        return null;
    }

    public void closeDB() {
        try {
            if (snappyDB.isOpen())
                snappyDB.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }

    }

    public String makeNewId(int i) {
        return context.getString(R.string.prefix_id) + (i + 1);
    }

    public void showProgress() {
        progressDialog.show();
    }

    public void dismissProgess() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


}
