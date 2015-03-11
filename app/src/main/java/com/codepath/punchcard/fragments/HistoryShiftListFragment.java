package com.codepath.punchcard.fragments;

import android.os.Bundle;
import android.util.Log;

import com.codepath.punchcard.models.Shift;
import com.codepath.punchcard.models.User;
import com.codepath.punchcard.models.UsersShift;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alvin on 3/11/15.
 */
public class HistoryShiftListFragment extends BaseShiftListFragment {

    public static HistoryShiftListFragment newInstance(User user) {
        HistoryShiftListFragment fragment = new HistoryShiftListFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    //Todo: Refactor logic into base class and remodel
    @Override
    protected void populateShifts() {
        User user = (User) getArguments().getSerializable("user");
        ParseQuery<UsersShift> query = ParseQuery.getQuery(UsersShift.class);
        query.orderByDescending("startTime");
        query.whereEqualTo("user", user);
        query.include("shift");
        query.findInBackground(new FindCallback<UsersShift>() {
            public void done(List<UsersShift> usersShifts, ParseException e) {
                if (e == null) {
                    Date curDate = new Date();
                    ArrayList<Shift> shifts = new ArrayList<>();
                    for (UsersShift userShift: usersShifts) {
                        if (userShift.getShift().getStartTime().before(curDate)) {
                            shifts.add(userShift.getShift());
                        }
                    }
                    HistoryShiftListFragment.this.aShifts.addAll(shifts);
                } else {
                    Log.d("message", "Error: " + e.getMessage());
                }
            }
        });
    }
}
