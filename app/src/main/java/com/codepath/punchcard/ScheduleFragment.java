package com.codepath.punchcard;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import com.codepath.punchcard.models.Weather;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class ScheduleFragment extends Fragment {
  private static final String ARG_SECTION_NUMBER = "section_number";
  private TextView weatherIcon;
  private Typeface weatherFont;

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
    client.get("http://api.openweathermap.org/data/2.5/weather?q=San%20Francisco,%20CA&units=metric", new JsonHttpResponseHandler(){
      @Override public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Weather weather = Weather.fromJSON(response, getActivity());
        weatherIcon.setText(weather.getIcon() + "  " + (weather.getTemp()));
      }
    });

  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
    setupCalendar(rootView);
    weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
    weatherIcon.setTypeface(weatherFont);
    return rootView;
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
        Toast.makeText(getActivity().getApplicationContext(), (month + 1)+ "/" + day  + "/" + year,
            Toast.LENGTH_SHORT).show();
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
