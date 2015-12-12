package com.mobiweb.expense.manager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vaibhav.labour.management.R;

import java.util.ArrayList;

/**
 * Created by vaibhav on 12/12/15.
 */
public class CustomArrayAdapter extends ArrayAdapter<String> {

    private ArrayList<String> objects;

    public CustomArrayAdapter(Context context, ArrayList<String> objects) {
        super(context, 0, objects);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.dialog_layout, null);
        }

        String i = objects.get(position);

        if (i != null) {
            TextView tt = (TextView) v.findViewById(R.id.text);
            tt.setText(i);
        }

        return v;
    }
}
