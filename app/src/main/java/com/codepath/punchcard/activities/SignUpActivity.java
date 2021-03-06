package com.codepath.punchcard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.codepath.punchcard.R;
import com.codepath.punchcard.adapters.PlacesAutoCompleteAdapter;
import com.codepath.punchcard.models.Company;
import com.codepath.punchcard.models.User;
import com.parse.ParseException;
import com.parse.SignUpCallback;

public class SignUpActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.actvCompanyAddress);
        autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    public void onSignUp(View view) {
        EditText etFirstName = (EditText) findViewById(R.id.etFirstName);
        EditText etLastName = (EditText) findViewById(R.id.etLastName);
        EditText etEmail = (EditText) findViewById(R.id.etEmail);
        EditText etPassword = (EditText) findViewById(R.id.etPassword);
        EditText etCompanyName = (EditText) findViewById(R.id.etCompanyName);
        AutoCompleteTextView actvCompanyAddress = (AutoCompleteTextView) findViewById(R.id.actvCompanyAddress);

        final User user = new User();
        user.setUsername(etEmail.getText().toString());
        user.setPassword(etPassword.getText().toString());
        user.setEmail(etEmail.getText().toString());
        user.put("firstName", etFirstName.getText().toString());
        user.put("lastName", etLastName.getText().toString());
        user.put("role", "manager");

        final Company company = new Company();
        company.setName(etCompanyName.getText().toString());
        company.setAddress(actvCompanyAddress.getText().toString());
        try {
            company.save();
            user.setCompany(company);
            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        startActivity(new Intent(SignUpActivity.this, EmployeesActivity.class));
                    } else {
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
