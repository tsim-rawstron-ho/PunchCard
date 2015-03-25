package com.codepath.punchcard.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.punchcard.R;
import com.codepath.punchcard.helpers.DateHelper;
import com.codepath.punchcard.models.Shift;
import com.codepath.punchcard.models.ShiftSession;
import com.codepath.punchcard.models.UsersShift;
import com.gc.materialdesign.views.ButtonRectangle;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShiftActivity extends ActionBarActivity {

    private static class SessionsAdapter extends ArrayAdapter<ShiftSession> {

    public SessionsAdapter(Context context, int resource) {
      super(context, resource);
    }

    public SessionsAdapter(Context context, int resource, int textViewResourceId) {
      super(context, resource, textViewResourceId);
    }

    public SessionsAdapter(Context context, int resource, ShiftSession[] objects) {
      super(context, resource, objects);
    }

    public SessionsAdapter(Context context, int resource, int textViewResourceId, ShiftSession[] objects) {
      super(context, resource, textViewResourceId, objects);
    }

    public SessionsAdapter(Context context, int resource, List<ShiftSession> objects) {
      super(context, resource, objects);
    }

    public SessionsAdapter(Context context, int resource, int textViewResourceId, List<ShiftSession> objects) {
      super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ShiftSession session = getItem(position);

      if (convertView == null) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.shift_session_cell, parent, false);
      }

        SimpleDateFormat sf = new SimpleDateFormat("MMM dd HH:mm", Locale.ENGLISH);
        String formattedStartTime = sf.format(session.getStartTime());

        ShiftSession.SessionType type = session.getType();
        String sessionType = "On Shift";
        switch (type) {
            case WORK:
                sessionType = "On Shift";
                break;
            case BREAK:
                sessionType = "On Break";
                break;
            default:break;
        }
        ((TextView) convertView.findViewById(R.id.tvSessionLabel)).setText(sessionType);
        ((TextView) convertView.findViewById(R.id.tvSessionTimes)).setText(formattedStartTime);

      return convertView;
    }
  }
  private List<UsersShift> shifts;
  private UsersShift currentUserShift;
  private ShiftSession workSession;
  private ShiftSession breakSession;
  private Chronometer chronometer;
    private LinearLayout llPauseStop;
  private long elaspedShiftTime;
  private long shiftStartTime;
  private boolean shiftInProgress = false;
  private boolean shiftPaused = false;
  private ArrayList<ShiftSession> sessions;
  private SupportMapFragment mapFragment;
    private ButtonRectangle btnPauseResume;
    private ButtonRectangle btnStart;
    private String shiftId;
    private Shift shift;
    private TextView tvShiftTime;
    private TextView tvShiftAddress;

  private ListView lvSession;
  private SessionsAdapter sessionsAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_shift);
    sessions = new ArrayList<>();
      btnStart = (ButtonRectangle) findViewById(R.id.btnStart);
      btnPauseResume = (ButtonRectangle) findViewById(R.id.btnPauseResume);
      llPauseStop = (LinearLayout) findViewById(R.id.llPauseStop);
    chronometer = (Chronometer) findViewById(R.id.chronometer);
      tvShiftAddress = (TextView) findViewById(R.id.tvShiftAddress);
      tvShiftTime = (TextView) findViewById(R.id.tvShiftTime);
    chronometer.stop();
    chronometer.setBase(SystemClock.elapsedRealtime());

    lvSession = (ListView) findViewById(R.id.lvSession);
      shiftId = getIntent().getStringExtra("shiftId");
    sessionsAdapter = new SessionsAdapter(this, R.layout.shift_session_cell, sessions);
    lvSession.setAdapter(sessionsAdapter);
    setUpMapIfNeeded();

      ParseQuery<Shift> query = ParseQuery.getQuery(Shift.class);
      query.whereEqualTo("objectId", shiftId);
      query.include("company");
      query.findInBackground(new FindCallback<Shift>() {
          @Override
          public void done(List<Shift> shifts, ParseException e) {
              shift = shifts.get(0);
              final ParseUser currentUser = ParseUser.getCurrentUser();
              getShifts(currentUser, new FindCallback<UsersShift>() {
                  @Override
                  public void done(List<UsersShift> usersShifts, ParseException e) {
                      if (usersShifts.size() > 0) {
                          currentUserShift = usersShifts.get(0);
                      }
                      init();
                  }
              });
          }
      });



  }

    private void init() {
        tvShiftTime.setText(DateHelper.formateShortDate(shift.getStartTime()) + ": " +
                shift.getStartTimeString() + " - " + shift.getEndTimeString());
        tvShiftAddress.setText(shift.getCompany().getAddress());
    }

    protected void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mapFragment == null) {
            mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            // Check if we were successful in obtaining the map.
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        loadMap(map);
                    }
                });
            }
        }
    }

    // The Map is verified. It is now safe to manipulate the map.
    protected void loadMap(GoogleMap googleMap) {
        if (googleMap != null) {
            double lat = 37.770344;
            double lng = -122.4038054;
            LatLng latLng = new LatLng(lat, lng);
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Zynga"));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            googleMap.animateCamera(cameraUpdate);
        }
    }

    public void onStart(View view) {
        startShift();
    }


    public void onEnd(View view) {
        endShift();
    }


    public void onPause(View view) {
        if (shiftPaused) {
            resumeShift();
        } else {
            pauseShift();
        }
    }

  public void getShifts(ParseUser user, FindCallback<UsersShift> callback) {
    ParseQuery<UsersShift> query = ParseQuery.getQuery(UsersShift.class);
    query.whereEqualTo("user", user);
//      query.whereEqualTo("shift", shift);
    query.findInBackground(callback);
  }

  private void startShift() {
      llPauseStop.setVisibility(View.VISIBLE);
      btnStart.setVisibility(View.GONE);
    shiftInProgress = true;
    shiftStartTime = SystemClock.elapsedRealtime();
    chronometer.setBase(shiftStartTime);
    chronometer.start();
    workSession = createNewWorkShift();
    saveSession(workSession);
    addSession(workSession);
  }

  private void pauseShift() {
      shiftPaused = true;
    elaspedShiftTime = SystemClock.elapsedRealtime() - chronometer.getBase();
    chronometer.stop();
    final Date timeToUTC = getUtcNowDate();
    breakSession = new ShiftSession();
    breakSession.setType(ShiftSession.SessionType.BREAK);
    breakSession.setUserShift(currentUserShift);
    breakSession.setStartTime(timeToUTC);
    workSession.setEndTime(timeToUTC);
    saveSession(workSession);
    saveSession(breakSession);
    addSession(breakSession);
      btnPauseResume.setText("Resume");
  }

  private void resumeShift() {
      shiftPaused = false;
    chronometer.setBase(SystemClock.elapsedRealtime() - elaspedShiftTime);
    chronometer.start();
    workSession = createNewWorkShift();
    breakSession.setEndTime(getUtcNowDate());
    saveSession(workSession);
    saveSession(breakSession);
      btnPauseResume.setText("Pause");
    addSession(workSession);
  }

  private void addSession(ShiftSession session) {
    sessions.add(0, session);
    sessionsAdapter.notifyDataSetChanged();
  }

  private void endShift() {
    shiftInProgress = false;
    workSession.setEndTime(getUtcNowDate());
      llPauseStop.setVisibility(View.GONE);
      LinearLayout llEnded = (LinearLayout) findViewById(R.id.llEnded);
      llEnded.setVisibility(View.VISIBLE);
    saveSession(workSession);
      chronometer.stop();
      sessionsAdapter.notifyDataSetChanged();
  }

  private ShiftSession createNewWorkShift() {
    final ShiftSession s = new ShiftSession();
    s.setStartTime(getUtcNowDate());
    s.setType(ShiftSession.SessionType.WORK);
    s.setUserShift(currentUserShift);
    return s;
  }

  private Date getUtcNowDate() {
    return DateHelper.localTimeToUTC(new Date());
  }

  private void saveSession(ShiftSession session) {
    try {
      session.save();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  private void showNextShift(UsersShift usersShift) {
//    showNextShift();
  }

}
