package com.codepath.punchcard;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.codepath.punchcard.adapters.SettingsAdapter;
import com.codepath.punchcard.helpers.DateHelper;
import com.codepath.punchcard.models.Shift;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.parse.ParseException;
import com.parse.SaveCallback;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;

public class CreateNewShiftActivity extends ActionBarActivity implements CalendarDatePickerDialog.OnDateSetListener, RadialTimePickerDialog.OnTimeSetListener {
  private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
  private ListView settingsList;
  private List<Pair<String, Object>> settings;
  private ArrayAdapter<Pair<String, Object>> settingsAdapter;
  private Shift shift;
  private static final String FRAG_TAG_START_TIME_PICKER = "startTimePickerDialogFragment";
  private static final String FRAG_TAG_END_TIME_PICKER = "endTimePickerDialogFragment";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_new_shift);
    setTitle("Create New Shift");
    settingsList = (ListView) findViewById(R.id.setting_list);
    createSettings();
    settingsAdapter = new SettingsAdapter<Pair<String, Object>>(this, android.R.layout.simple_list_item_1, settings);
    reloadData();
    settingsList.setAdapter(settingsAdapter);
    settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DateTime now = DateTime.now();

        switch (position) {
          case 0:
            // not implemented yet
            break;
          case 1:
            FragmentManager fm = getSupportFragmentManager();
            CalendarDatePickerDialog calendarDatePickerDialog =
                CalendarDatePickerDialog.newInstance(CreateNewShiftActivity.this, now.getYear(),
                    now.getMonthOfYear() - 1, now.getDayOfMonth());
            calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
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

  private void createSettings() {
    shift = new Shift();
    shift.setStartTime(new Date());
    shift.setEndTime(new Date());
    settings = new ArrayList<>();
  }

  private void reloadData() {
    List<String> employeeNames = new ArrayList<>();
    employeeNames.add("Harris");
    employeeNames.add("Ash");
    employeeNames.add("Alvin");
    settingsAdapter.clear();
    settingsAdapter.add(new Pair<String, Object>(null, (Object) employeeNames));
    settingsAdapter.add(new Pair<String, Object>(null, (Object)shift.getStartTime()));
    settingsAdapter.add(new Pair<String, Object>("Start Shift", (Object)shift.getStartTime()));
    settingsAdapter.add(new Pair<String, Object>("End Shift", (Object)shift.getEndTime()));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_create_new_shift, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_create) {
      saveAndReload();
      return true;
    }

    return super.onOptionsItemSelected(item);
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
        Toast.makeText(CreateNewShiftActivity.this, "Shift Created", Toast.LENGTH_LONG).show();
        reloadData();
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
}
