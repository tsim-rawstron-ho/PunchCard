package com.codepath.punchcard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.codepath.punchcard.activities.CreateNewShiftActivity;
import com.codepath.punchcard.activities.EmployeesActivity;
import com.codepath.punchcard.activities.LoginActivity;
import com.codepath.punchcard.activities.ProfileActivity;
import com.codepath.punchcard.activities.ShiftActivity;
import com.codepath.punchcard.adapters.ShiftAdapter;
import com.codepath.punchcard.fragments.UpdateProfileFragment;
import com.codepath.punchcard.helpers.DateHelper;
import com.codepath.punchcard.models.Company;
import com.codepath.punchcard.models.Shift;
import com.codepath.punchcard.models.User;
import com.codepath.punchcard.models.Weather;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.ParseUser;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.http.Header;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity implements  UpdateProfileFragment.OnFragmentInteractionListener {
  private static final String ARG_SECTION_NUMBER = "section_number";
  private TextView weatherIcon;
  private Typeface weatherFont;
  private Weather weather;
  private ListView shiftListView;
  private ShiftAdapter shiftAdapter;
  private List<Shift> shifts;
  private CaldroidFragment caldroidFragment;
  private Date selectedDate = null;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    weatherFont = Typeface.createFromAsset(this.getAssets(), "fonts/weather.ttf");

    AsyncHttpClient client = new AsyncHttpClient();
    try {
      client.get(String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&APPID=e7e8c692f8e98d6069f3742c0de25a6c",
          URLEncoder.encode("San Francisco, us", "UTF8")), new JsonHttpResponseHandler(){
        @Override public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
          weather = Weather.fromJSON(response, MainActivity.this);
          weatherIcon.setText(weather.getIcon() + "  " + (weather.getTemp()));
        }
      });
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    setContentView(R.layout.activity_main);
    weatherIcon = (TextView)findViewById(R.id.weather_icon);
    weatherIcon.setTypeface(weatherFont);
    setupShiftList((Activity) this);
    setupCalendar(savedInstanceState);
  }

  private void setupCalendar(Bundle savedInstanceState) {
    caldroidFragment = new CaldroidFragment();
    if (savedInstanceState != null) {
      caldroidFragment.restoreStatesFromKey(savedInstanceState,
          "CALDROID_SAVED_STATE");
    } else {
      Bundle args = new Bundle();
      Calendar cal = Calendar.getInstance();
      args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
      args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
      args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
      args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
      args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);

      caldroidFragment.setArguments(args);
    }
    FragmentTransaction t = getSupportFragmentManager().beginTransaction();
    t.replace(R.id.calendar, caldroidFragment);
    t.commit();
    final CaldroidListener listener = new CaldroidListener() {
      @Override
      public void onSelectDate(Date date, View view) {
        selectDate(date);
      }
    };
    caldroidFragment.setCaldroidListener(listener);
    selectDate(new Date());
  }

  private void selectDate(Date date) {
    if (selectedDate != null) {
      caldroidFragment.setBackgroundResourceForDate(R.color.white, selectedDate);
      caldroidFragment.setTextColorForDate(R.color.caldroid_black, selectedDate);
    }

    selectedDate = date;
    loadShiftsFor(selectedDate);
    caldroidFragment.setBackgroundResourceForDate(R.color.blue, selectedDate);
    caldroidFragment.setTextColorForDate(R.color.white, selectedDate);
    caldroidFragment.refreshView();

    if (weather == null) {
      weatherIcon.setText(DateHelper.formateDate(selectedDate));
    } else {
      weatherIcon.setText(weather.getIcon()
          + "  "
          + (weather.getTemp())
          + "   "
          + DateHelper.formateDate(selectedDate));
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override public boolean onPrepareOptionsMenu(Menu menu) {
    MenuItem viewEmployee = menu.findItem(R.id.action_employees);
    MenuItem createShift = menu.findItem(R.id.action_create_shift);
    User currentUser = (User) ParseUser.getCurrentUser();
    if (currentUser.isManager()) {
      if (createShift != null)
        createShift.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
      if (viewEmployee != null)
        viewEmployee.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
    return  super.onPrepareOptionsMenu(menu);
  }

  private void loadShiftsFor(Date selectedDate) {
    ((User)ParseUser.getCurrentUser()).getCompany().getShifts(new Company.CompanyListener() {
      @Override public void shiftsFetched(List<Shift> shifts) {
        shiftAdapter.clear();
        shiftAdapter.addAll(shifts);
      }
    });
  }

  private void setupShiftList(Activity rootView) {
    shifts = new ArrayList<Shift>();
    shiftListView = (ListView) rootView.findViewById(R.id.shifts_list);
    shiftAdapter = new ShiftAdapter<Shift>(this, android.R.layout.simple_list_item_1, shifts);
    shiftListView.setAdapter(shiftAdapter);
    shiftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(MainActivity.this, ShiftActivity.class);
        startActivity(i);
      }
    });
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
        } else if (id == R.id.action_logout) {
            ParseUser.getCurrentUser().logOut();
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.action_create_shift) {
          Intent intent = new Intent(this, CreateNewShiftActivity.class);
          startActivity(intent);
        } else if (id == R.id.action_profile) {
          Intent intent = new Intent(this, ProfileActivity.class);
          startActivity(intent);
        } else if (id == R.id.action_employees) {
            Intent intent = new Intent(this, EmployeesActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProfileUpdated(String firstName, String lastName, String username) {
    }
}
