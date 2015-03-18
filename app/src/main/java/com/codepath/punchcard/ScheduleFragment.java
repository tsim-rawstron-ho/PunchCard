package com.codepath.punchcard;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import com.codepath.punchcard.adapters.ShiftAdapter;
import com.codepath.punchcard.helpers.DateHelper;
import com.codepath.punchcard.models.Company;
import com.codepath.punchcard.models.Shift;
import com.codepath.punchcard.models.User;
import com.codepath.punchcard.models.Weather;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.ParseUser;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.Header;
import org.json.JSONObject;

public class ScheduleFragment extends Fragment {
  private static final String ARG_SECTION_NUMBER = "section_number";
  private TextView weatherIcon;
  private Typeface weatherFont;
  private Weather weather;
  private ListView shiftListView;
  private ShiftAdapter shiftAdapter;
  private List<Shift> shifts;
  private CalendarView calendar;

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
      client.get(String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&APPID=e7e8c692f8e98d6069f3742c0de25a6c",
          URLEncoder.encode("San Francisco, us", "UTF8")), new JsonHttpResponseHandler(){
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
    if (currentUser.isManager()) {
      menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
  }

  private void setupShiftList(View rootView) {
    shifts = new ArrayList<Shift>();
    shiftListView = (ListView) rootView.findViewById(R.id.shifts_list);
    shiftAdapter = new ShiftAdapter<Shift>(getActivity(), android.R.layout.simple_list_item_1, shifts);
    shiftListView.setAdapter(shiftAdapter);
  }

  private void setupCalendar(View rootView) {
    calendar = (CalendarView)rootView.findViewById(R.id.calendarView);
    calendar.setSelectedWeekBackgroundColor(getResources().getColor(R.color.blue));
    calendar.setUnfocusedMonthDateColor(getResources().getColor(R.color.transparent));
    calendar.setWeekSeparatorLineColor(getResources().getColor(R.color.transparent));
    calendar.setSelectedDateVerticalBar(R.color.darkblue);
    calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
      @Override public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
        loadShiftsFor(DateHelper.getSelectedDate(year, month, day));
        if (weather == null) {
          weatherIcon.setText(DateHelper.getSelectedDateString(year, month, day));
        } else {
          weatherIcon.setText(weather.getIcon()
              + "  "
              + (weather.getTemp())
              + "   "
              + DateHelper.getSelectedDateString(year, month, day));
        }
      }
    });
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    MenuItem item = menu.findItem(R.id.action_create_shift);
    if (((User)ParseUser.getCurrentUser()).isManager()) {
      item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    } else {
      item.setVisible(false);
    }
    super.onCreateContextMenu(menu, v, menuInfo);
  }

  private void loadShiftsFor(Date selectedDate) {
    ((User)ParseUser.getCurrentUser()).getCompany().getShifts(new Company.CompanyListener() {
      @Override public void shiftsFetched(List<Shift> shifts) {
        shiftAdapter.clear();
        shiftAdapter.addAll(shifts);
      }
    });
  }

  @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
