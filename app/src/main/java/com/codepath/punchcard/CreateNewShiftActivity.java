package com.codepath.punchcard;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import org.joda.time.DateTime;

public class CreateNewShiftActivity extends ActionBarActivity implements CalendarDatePickerDialog.OnDateSetListener {
  private Button button;
  private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_new_shift);

    button = (Button) findViewById(R.id.button);

    button.setText("Set Date");
    button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        FragmentManager fm = getSupportFragmentManager();
        DateTime now = DateTime.now();
        CalendarDatePickerDialog calendarDatePickerDialog =
            CalendarDatePickerDialog.newInstance(CreateNewShiftActivity.this, now.getYear(),
                now.getMonthOfYear() - 1, now.getDayOfMonth());
        calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
      }
    });
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
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int i, int i1, int i2) {

  }
}
