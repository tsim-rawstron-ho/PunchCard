package com.codepath.punchcard.fragments;

import android.os.Bundle;

import com.codepath.punchcard.models.Shift;
import com.codepath.punchcard.models.User;

import java.util.ArrayList;
import java.util.Date;

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

    @Override
    protected void filterShifts(ArrayList<Shift> shifts) {
        Date d = new Date();
        for (Shift s: shifts) {
            if (d.after(s.getStartTime())) {
                this.aShifts.add(s);
            }
        }
    }
}
