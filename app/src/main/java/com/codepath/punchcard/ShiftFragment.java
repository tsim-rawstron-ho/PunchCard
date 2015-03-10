package com.codepath.punchcard;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import com.codepath.punchcard.models.ShiftSession;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ShiftFragment extends Fragment implements LocationListener {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private GoogleMap googleMap;
    private MapView mapView;
    private Button btnStart;
    private Button btnEnd;
    private View rlStartShift;
    private View rlInProgres;
    private View tvEndShift;
    private ToggleButton tglPause;
    private ArrayList<ShiftSession> shiftSessions;

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
        
        shiftSessions = new ArrayList<>();

        btnStart = (Button) rootView.findViewById(R.id.btnStart);
        btnEnd = (Button) rootView.findViewById(R.id.btnEnd);
        tglPause = (ToggleButton) rootView.findViewById(R.id.btnPause);
        rlStartShift = rootView.findViewById(R.id.rlStartShift);
        rlInProgres = rootView.findViewById(R.id.rlInProgres);
        tvEndShift = rootView.findViewById(R.id.tvEndShift);
        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        googleMap = mapView.getMap();
        
        
        // Event listeners:
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setVisibility(View.GONE);
                rlStartShift.setVisibility(View.GONE);
                btnEnd.setVisibility(View.VISIBLE);
                rlInProgres.setVisibility(View.VISIBLE);
                startShift();
            }
        });
        
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvEndShift.setVisibility(View.VISIBLE);
                tglPause.setVisibility(View.GONE);
                btnEnd.setVisibility(View.GONE);
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
        return rootView;
    }

    private void startShift() {
        ShiftSession s = new ShiftSession();
//        s.setCompany("Harris Co.");
    }

    private void pauseShift() {

    }

    private void resumeShift() {

    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 20);
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
