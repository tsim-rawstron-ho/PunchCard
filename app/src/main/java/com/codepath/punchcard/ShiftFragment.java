package com.codepath.punchcard;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.codepath.punchcard.helpers.DateHelper;
import com.codepath.punchcard.models.Shift;
import com.codepath.punchcard.models.ShiftSession;
import com.codepath.punchcard.models.UsersShift;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShiftFragment extends Fragment implements LocationListener, SlideToUnlock.OnUnlockListener {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private GoogleMap googleMap;
    private MapView mapView;
    private Button btnStart;
    private Button btnEnd;
    private View rlStartShift;
    private View rlInProgres;
    private ToggleButton tglPause;
    private List<UsersShift> shifts;
    private UsersShift currentShift;
    private ShiftSession workSession;
    private ShiftSession breakSession;
    private Chronometer chronometer;
    private SlideToUnlock slideToUnlock;
    private long elaspedShiftTime;
    private long shiftStartTime;
    private long pauseTime;
    private boolean shiftInProgress = false;
    private View rlInShiftControls;
    private ArrayList<ShiftSession> sessions;

    private ListView lvSession;
    private SessionsAdapter sessionsAdapter;

    public static ShiftFragment newInstance(int sectionNumber) {
        ShiftFragment fragment = new ShiftFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ShiftFragment() {
    }

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

            ((TextView) convertView.findViewById(R.id.tvSession)).setText(session.getType().toString());

            return convertView;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shift, container, false);
        FragmentActivity activity = getActivity();
        sessions = new ArrayList<>();

        chronometer = (Chronometer) rootView.findViewById(R.id.chronometer);
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());

        lvSession = (ListView) rootView.findViewById(R.id.lvSession);
        rlInShiftControls = rootView.findViewById(R.id.rlInShiftControls);
        tglPause = (ToggleButton) rootView.findViewById(R.id.btnPause);
        slideToUnlock = (SlideToUnlock) rootView.findViewById(R.id.slidetounlock);
        rlStartShift = rootView.findViewById(R.id.rlNextShift);
        rlInProgres = rootView.findViewById(R.id.rlInProgress);

        sessionsAdapter = new SessionsAdapter(activity, R.layout.shift_session_cell, sessions);
        lvSession.setAdapter(sessionsAdapter);

        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        googleMap = mapView.getMap();

        // Event listeners:
        slideToUnlock.setOnUnlockListener(this);
        tglPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tglPause.isChecked()) {
                    pauseShift();
                } else {
                    resumeShift();
                }
            }
        });

        // Initialize map at current location:
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMyLocationEnabled(true);
        MapsInitializer.initialize(activity);
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(provider, 20000, 0, this);

        // Get User Shifts Info:
        final ParseUser currentUser = ParseUser.getCurrentUser();
        getShifts(currentUser, new FindCallback<UsersShift>() {
            @Override
            public void done(List<UsersShift> usersShifts, ParseException e) {
                shifts = usersShifts;
                currentShift = shifts.get(0);

                final Shift shift = currentShift.getShift();
//                final Date startTime = shift.getStartTime();
//                final long shiftDiff = currentShift.getShift().getEndTime().getTime() - currentShift.getShift().getStartTime().getTime();
//                final long shiftSeconds = shiftDiff / 1000;
//                final long shiftMinutes = shiftSeconds / 60;

                showNextShift(currentShift);
            }
        });
        return rootView;
    }

    @Override
    public void onUnlock() {
        if (!shiftInProgress) {
            startShift();
            showCurrentShift();
        } else {
            endShift();
            showNextShift();
        }
    }

    public void getShifts(ParseUser user, FindCallback<UsersShift> callback) {
        ParseQuery<UsersShift> query = ParseQuery.getQuery(UsersShift.class);
        query.orderByAscending("startTime");
        query.whereEqualTo("user", user);
        query.findInBackground(callback);
    }

    private void startShift() {
        shiftInProgress = true;
        shiftStartTime = SystemClock.elapsedRealtime();
        chronometer.setBase(shiftStartTime);
        chronometer.start();
        workSession = createNewWorkShift();
        saveSession(workSession);

        addSession(workSession);
    }

    private void pauseShift() {
        elaspedShiftTime = SystemClock.elapsedRealtime() - chronometer.getBase();
        chronometer.stop();
        final Date timeToUTC = getUtcNowDate();
        breakSession = new ShiftSession();
        breakSession.setType(ShiftSession.SessionType.BREAK);
        breakSession.setUserShift(currentShift);
        breakSession.setStartTime(timeToUTC);
        workSession.setEndTime(timeToUTC);
        saveSession(workSession);
        saveSession(breakSession);

        addSession(breakSession);
    }

    private void resumeShift() {
        chronometer.setBase(SystemClock.elapsedRealtime() - elaspedShiftTime);
        chronometer.start();
        workSession = createNewWorkShift();
        breakSession.setEndTime(getUtcNowDate());
        saveSession(workSession);
        saveSession(breakSession);

        addSession(workSession);
    }

    private void addSession(ShiftSession session) {
        sessions.add(0, session);
        sessionsAdapter.notifyDataSetChanged();
    }

    private void endShift() {
        shiftInProgress = false;
        slideToUnlock.setLabelText(getString(R.string.start_shift));
        workSession.setEndTime(getUtcNowDate());
        saveSession(workSession);
    }

    private ShiftSession createNewWorkShift() {
        final ShiftSession s = new ShiftSession();
        s.setStartTime(getUtcNowDate());
        s.setType(ShiftSession.SessionType.WORK);
        s.setUserShift(currentShift);
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
        showNextShift();
    }

    private void showNextShift() {
        slideToUnlock.setLabelText(getString(R.string.start_shift));
        slideToUnlock.reset();
        rlStartShift.setVisibility(View.VISIBLE);
        rlInProgres.setVisibility(View.GONE);
        rlInShiftControls.setVisibility(View.GONE);
    }

    private void showCurrentShift() {
        slideToUnlock.setLabelText(getString(R.string.end_shift));
        slideToUnlock.reset();
        rlStartShift.setVisibility(View.GONE);
        rlInProgres.setVisibility(View.VISIBLE);
        rlInShiftControls.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        googleMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    //@Override
    //public void onAttach(Activity activity) {
    //    super.onAttach(activity);
    //    ((MainActivity) activity).onSectionAttached(
    //            getArguments().getInt(ARG_SECTION_NUMBER));
    //}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
