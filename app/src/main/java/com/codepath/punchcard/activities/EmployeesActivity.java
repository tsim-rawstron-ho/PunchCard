package com.codepath.punchcard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import com.codepath.punchcard.MainActivity;
import com.codepath.punchcard.R;
import com.codepath.punchcard.adapters.EmployeesArrayAdapter;
import com.codepath.punchcard.models.User;
import com.gc.materialdesign.views.ButtonFloat;
import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import dialogs.InviteEmployeeDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmployeesActivity extends ActionBarActivity implements InviteEmployeeDialog.OnInviteEmployee {
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private ListView lvEmployees;
    private ArrayList<User> employees;
    private EmployeesArrayAdapter aEmployees;
    private FloatingActionButton btnAddEmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        employees = new ArrayList<>();
        lvEmployees = (ListView) findViewById(R.id.lvEmployees);
        aEmployees = new EmployeesArrayAdapter(this, employees);
        lvEmployees.setAdapter(aEmployees);
        populateEmployees();


        btnAddEmp = (FloatingActionButton) findViewById(R.id.btnFltAdd);
        btnAddEmp.setOnClickListener(new ButtonFloat.OnClickListener() {
            @Override
            public void onClick(View view) {
                InviteEmployeeDialog dialog = InviteEmployeeDialog.newInstance();
                FragmentManager fm = getSupportFragmentManager();
                dialog.show(fm, "fragment_invite_employee");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invite_employee, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_finish) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    public void inviteEmployee(String email, String firstName, String lastName) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("email", email);
        params.put("managerId", ParseUser.getCurrentUser().getObjectId());

        ParseCloud.callFunctionInBackground("add_employee", params, new FunctionCallback<String>() {
            public void done(String result, ParseException e) {
                if (e == null) {
                    Crouton.makeText(EmployeesActivity.this, result, Style.CONFIRM).show();
                    populateEmployees();
                }
            }
        });
    }

    private void populateEmployees() {
        ParseQuery<User> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("company", ParseUser.getCurrentUser().get("company"));
        query.whereEqualTo("role", "employee");
        query.findInBackground(new FindCallback<User>() {
            public void done(List<User> userList, ParseException e) {
                if (e == null) {
                    aEmployees.clear();
                    aEmployees.addAll(userList);
                } else {
                    Log.d("users", "Error: " + e.getMessage());
                }
            }
        });

    }
}
