package com.codepath.punchcard.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.codepath.punchcard.R;
import com.codepath.punchcard.adapters.SettingsAdapter;
import com.codepath.punchcard.fragments.EmployeePickerFragment;
import com.codepath.punchcard.helpers.DateHelper;
import com.codepath.punchcard.models.Shift;
import com.codepath.punchcard.models.User;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;

public class CreateNewShiftActivity extends ActionBarActivity implements CalendarDatePickerDialog.OnDateSetListener, RadialTimePickerDialog.OnTimeSetListener,
    EmployeePickerFragment.UserChosenListener {
  private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
  private ListView settingsList;
  private List<Pair<String, Object>> settings;
  private ArrayAdapter<Pair<String, Object>> settingsAdapter;
  private Shift shift;
  private static final String FRAG_TAG_START_TIME_PICKER = "startTimePickerDialogFragment";
  private static final String FRAG_TAG_END_TIME_PICKER = "endTimePickerDialogFragment";
  public static final String FRAG_TAG_EMPLOYEE_PICKER = "employDialogFragment";
  private List<User> pickedUsers;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_new_shift);
    setTitle("Create New Shift");
    settingsList = (ListView) findViewById(R.id.setting_list);
    createNewShift();
    settingsAdapter = new SettingsAdapter<Pair<String, Object>>(this, android.R.layout.simple_list_item_1, settings);
    pickedUsers = new ArrayList<User>();
    reloadData();

    Button createShift = (Button)findViewById(R.id.btn_create);
    createShift.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        saveAndReload();
      }
    });
    settingsList.setAdapter(settingsAdapter);
    settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DateTime now = DateTime.now();

        switch (position) {
          case 0:
            EmployeePickerFragment employeePickerFragment = EmployeePickerFragment.newInstance();
            employeePickerFragment.setListener(CreateNewShiftActivity.this);
            employeePickerFragment.show(getSupportFragmentManager(), FRAG_TAG_EMPLOYEE_PICKER);
            break;
          case 1:
            CalendarDatePickerDialog calendarDatePickerDialog =
                CalendarDatePickerDialog.newInstance(CreateNewShiftActivity.this, now.getYear(),
                    now.getMonthOfYear() - 1, now.getDayOfMonth());
            calendarDatePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
            break;
          case 2:
          case 3:
            String timeTag = FRAG_TAG_START_TIME_PICKER;
            if (position == 3) {
              timeTag = FRAG_TAG_END_TIME_PICKER;
            }
            RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                .newInstance(CreateNewShiftActivity.this, now.getHourOfDay(), now.getMinuteOfHour(),
                    DateFormat.is24HourFormat(CreateNewShiftActivity.this));
            timePickerDialog.show(getSupportFragmentManager(), timeTag);
            break;
        }
      }
    });

  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        supportFinishAfterTransition();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void createNewShift() {
    shift = new Shift();
    shift.setStartTime(new Date());
    shift.setEndTime(new Date());
    shift.setCompany(((User)ParseUser.getCurrentUser()).getCompany());
    settings = new ArrayList<>();
  }

  private void reloadData() {
    settingsAdapter.clear();
    settingsAdapter.add(new Pair<String, Object>(null, (Object) pickedUsers));
    settingsAdapter.add(new Pair<String, Object>("Date", (Object)shift.getStartTime()));
    settingsAdapter.add(new Pair<String, Object>("Start Shift", (Object)shift.getStartTime()));
    settingsAdapter.add(new Pair<String, Object>("End Shift", (Object)shift.getEndTime()));
  }

  public List<User> getPickedUsers() {
    return pickedUsers;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_create_new_shift, menu);
    return true;
  }

  @Override
  public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
      shift.setStartTime(DateHelper.parseDate((monthOfYear + 1) + "/" + dayOfMonth + "/" + year));
      shift.setEndTime(DateHelper.parseDate((monthOfYear + 1) + "/" + dayOfMonth + "/" + year));
      reloadData();
  }

  private void saveAndReload() {
    shift.saveInBackground(new SaveCallback() {
      @Override public void done(ParseException e) {
        reloadData();
        for (User pickedUser : pickedUsers) {
          shift.addUser(pickedUser);
        }
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.transition.enter_from_right,
            R.transition.exit_from_right);
      }
    });
  }

  @Override
  public void onTimeSet(RadialTimePickerDialog dialog, int hourOfDay, int minute) {
    if (dialog.getTag().equals(FRAG_TAG_START_TIME_PICKER)) {
      Date startTime = shift.getStartTime();
      startTime.setHours(hourOfDay);
      startTime.setMinutes(minute);
      shift.setStartTime(startTime);
    } else {
      Date endTime = shift.getEndTime();
      endTime.setHours(hourOfDay);
      endTime.setMinutes(minute);
      shift.setEndTime(endTime);
    }
    reloadData();
  }

  @Override public void userChosen(List<User> users) {
    pickedUsers = users;
    reloadData();
  }
}
