package com.mobiweb.expense.manager.activites;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.vaibhav.labour.management.R;
import com.mobiweb.expense.manager.utils.Global;

public class LoginActivity extends Activity {


    Global global;
    CheckBox checkBox;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("LM", Context.MODE_PRIVATE);
        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        checkBox = (CheckBox) findViewById(R.id.checkbox);

        global = new Global(this, "LoginActivity");


        if (sharedPreferences.getBoolean("checked", false) == true) {
            Intent intent = new Intent(LoginActivity.this, EmployeeName.class);
            startActivity(intent);
            finish();
        }

        Button submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (username.getText().length() == 0)
                    global.toast("please enter username");
                else if (password.getText().length() == 0)
                    global.toast("please enter password");
                else if (username.getText().toString().equalsIgnoreCase(getString(R.string.username)) && password.getText().toString().equalsIgnoreCase(getString(R.string.pass))) {

                    if (checkBox.isChecked()) {
                        sharedPreferences.edit().putBoolean("checked", true).apply();
                        sharedPreferences.edit().putString("username", username.getText().toString()).apply();
                        sharedPreferences.edit().putString("password", password.getText().toString()).apply();

                    }


                    Intent intent = new Intent(LoginActivity.this, EmployeeName.class);
                    startActivity(intent);
                    finish();

                } else {
                    global.toast("incorrect username or password");

                }
            }
        });


    }


}
