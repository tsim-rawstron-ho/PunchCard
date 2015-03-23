package com.codepath.punchcard.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.punchcard.R;
import com.codepath.punchcard.adapters.EmployeeShiftAdapter;
import com.codepath.punchcard.models.Shift;
import com.codepath.punchcard.models.User;
import com.codepath.punchcard.models.UsersShift;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class BaseShiftListFragment extends Fragment {
    protected EmployeeShiftAdapter aShifts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aShifts = new EmployeeShiftAdapter(getActivity(), new ArrayList<Shift>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shift_list, container, false);
        ListView lvShifts = (ListView) v.findViewById(R.id.lvShifts);
        lvShifts.setAdapter(aShifts);
        populateShifts();
        return v;
    }

    protected abstract void filterShifts(ArrayList<Shift> shifts);

    protected void populateShifts() {
        User user = (User) getArguments().getSerializable("user");
        ParseQuery<UsersShift> query = ParseQuery.getQuery(UsersShift.class);
        query.whereEqualTo("user", user);
        query.include("shift");
        query.findInBackground(new FindCallback<UsersShift>() {
            public void done(List<UsersShift> usersShifts, ParseException e) {
                if (e == null) {
                    ArrayList<Shift> shifts = new ArrayList<>();
                    for (UsersShift userShift: usersShifts) {
                        shifts.add(userShift.getShift());
                    }
                    Collections.sort(shifts, new Comparator<Shift>() {
                        @Override
                        public int compare(Shift lhs, Shift rhs) {
                            return lhs.getStartTime().compareTo(rhs.getEndTime());
                        }
                    });
                    filterShifts(shifts);
                } else {
                    Log.d("message", "Error: " + e.getMessage());
                }
            }
        });

    }
}
