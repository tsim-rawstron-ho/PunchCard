package com.codepath.punchcard;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.codepath.punchcard.adapters.ShiftAdapter;
import com.codepath.punchcard.models.Shift;
import com.codepath.punchcard.models.User;
import com.codepath.punchcard.models.Weather;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.ParseUser;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class ScheduleFragment extends Fragment {
  private static final String ARG_SECTION_NUMBER = "section_number";
  private TextView weatherIcon;
  private Typeface weatherFont;
  private Weather weather;
  private ListView shiftListView;
  private ShiftAdapter shiftAdapter;

  public static ScheduleFragment newInstance(int sectionNumber) {
      ScheduleFragment fragment = new ScheduleFragment();
      Bundle args = new Bundle();
      args.putInt(ARG_SECTION_NUMBER, sectionNumber);
      fragment.setArguments(args);
      return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");

    AsyncHttpClient client = new AsyncHttpClient();
    try {
      client.get(String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric",
          URLEncoder.encode("San Francisco, CA", "UTF8")), new JsonHttpResponseHandler(){
        @Override public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
          weather = Weather.fromJSON(response, getActivity());
          weatherIcon.setText(weather.getIcon() + "  " + (weather.getTemp()));
        }
      });
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
    setupCalendar(rootView);
    weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
    weatherIcon.setTypeface(weatherFont);
    setupShiftList(rootView);
    return rootView;
  }

  @Override public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    User currentUser = (User) ParseUser.getCurrentUser();
    if (currentUser.canCreateShift()) {
      menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
  }

  private void setupShiftList(View rootView) {
    Shift shift1 = new Shift();
    Shift shift2 = new Shift();
    List<Shift> shifts = new ArrayList<Shift>();
    shifts.add(shift1);
    shifts.add(shift2);

    shiftListView = (ListView) rootView.findViewById(R.id.shifts_list);
    shiftAdapter = new ShiftAdapter<>(getActivity(), android.R.layout.simple_list_item_1, shifts);
    shiftListView.setAdapter(shiftAdapter);
  }

  private Date parseDate(String dateString) {
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    Date d = null;
    try {
      d = formatter.parse(dateString);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return d;
  }
  
  private void setupCalendar(View rootView) {
    CalendarView calendar = (CalendarView)rootView.findViewById(R.id.calendarView);
    calendar.setSelectedWeekBackgroundColor(getResources().getColor(R.color.blue));
    calendar.setUnfocusedMonthDateColor(getResources().getColor(R.color.transparent));
    calendar.setWeekSeparatorLineColor(getResources().getColor(R.color.transparent));
    calendar.setSelectedDateVerticalBar(R.color.darkblue);
    calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
      @Override
      public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
        weatherIcon.setText(weather.getIcon() + "  " + (weather.getTemp()) + "   " + getSelectedDateString(year, month, day));
      }
    });
  }

  private String getSelectedDateString(int year, int month, int day) {
    Date selectedDate = parseDate((month + 1) + "/" + day + "/" + year);
    Date date = new Date();
    if (year % 100 == date.getYear() % 100 && month == date.getMonth() && day == date.getDay() + 1) {
      return "Today";
    } else {
      DateFormat df = new SimpleDateFormat("EEE, MMM d", Locale.US);
      return df.format(selectedDate);
    }
  }

  @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
