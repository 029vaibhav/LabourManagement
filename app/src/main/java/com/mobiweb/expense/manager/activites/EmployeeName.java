package com.mobiweb.expense.manager.activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mobiweb.expense.manager.utils.Constant;
import com.vaibhav.labour.management.R;
import com.mobiweb.expense.manager.fragments.EmployeesFragment;

public class EmployeeName extends AppCompatActivity {

    long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment fragment = new EmployeesFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.logOut:

                getSharedPreferences("LM", Context.MODE_PRIVATE).edit().putBoolean("checked", false).apply();
                Intent intent = new Intent(EmployeeName.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {


        if (Constant.currentFragment.equalsIgnoreCase("ViewPaymentFragment")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new EmployeesFragment()).commit();
        } else {
            if (back_pressed + 2000 > System.currentTimeMillis())
                super.onBackPressed();

            else
                Toast.makeText(getBaseContext(), "Press once again to exit!",
                        Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }

    }
}
