package com.codepath.punchcard;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.codepath.punchcard.helpers.DateHelper;
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
import java.util.Date;
import java.util.List;

public class ShiftFragment extends Fragment implements LocationListener {
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
    private TextView tvTime;

    public static ShiftFragment newInstance(int sectionNumber) {
        ShiftFragment fragment = new ShiftFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ShiftFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shift, container, false);
        FragmentActivity activity = getActivity();

        tvTime = (TextView) rootView.findViewById(R.id.tvTime);
        tglPause = (ToggleButton) rootView.findViewById(R.id.btnPause);
        btnStart = (Button) rootView.findViewById(R.id.btnStart);
        btnEnd = (Button) rootView.findViewById(R.id.btnEnd);
        rlStartShift = rootView.findViewById(R.id.rlNextShift);
        rlInProgres = rootView.findViewById(R.id.rlInProgres);

        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        googleMap = mapView.getMap();

        // Event listeners:
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrentShift();
                startShift();
            }
        });
        
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endShift();
                showNextShift();
            }
        });
        
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
        googleMap. setMyLocationEnabled(true);
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
                showNextShift(currentShift);
            }
        });

        timerHandler.postDelayed(timerRunnable, 0);
        return rootView;
    }

    private long workSeconds = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            workSeconds ++;
            tvTime.setText("time: " + workSeconds);
            timerHandler.postDelayed(this, 1000);
        }
    };

    public void getShifts(ParseUser user, FindCallback<UsersShift> callback) {
        ParseQuery<UsersShift> query = ParseQuery.getQuery(UsersShift.class);
        query.orderByAscending("startTime");
        query.whereEqualTo("user", user);
        query.findInBackground(callback);
    }

    private void startShift() {
        workSession = createNewWorkShift();
        saveSession(workSession);
    }

    private void pauseShift() {
        final Date timeToUTC = getUtcNowDate();
        breakSession = new ShiftSession();
        breakSession.setType(ShiftSession.SessionType.BREAK);
        breakSession.setUserShift(currentShift);
        breakSession.setStartTime(timeToUTC);
        workSession.setEndTime(timeToUTC);
        saveSession(workSession);
        saveSession(breakSession);
    }

    private void resumeShift() {
        workSession = createNewWorkShift();
        breakSession.setEndTime(getUtcNowDate());
        saveSession(workSession);
        saveSession(breakSession);
    }

    private void endShift() {
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
        btnStart.setVisibility(View.VISIBLE);
        rlStartShift.setVisibility(View.VISIBLE);
        btnEnd.setVisibility(View.GONE);
        rlInProgres.setVisibility(View.GONE);
    }

    private void showCurrentShift() {
        btnStart.setVisibility(View.GONE);
        rlStartShift.setVisibility(View.GONE);
        btnEnd.setVisibility(View.VISIBLE);
        rlInProgres.setVisibility(View.VISIBLE);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

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
