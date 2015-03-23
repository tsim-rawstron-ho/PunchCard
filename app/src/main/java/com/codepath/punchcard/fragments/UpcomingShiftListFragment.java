package com.codepath.punchcard.fragments;

import android.os.Bundle;

import com.codepath.punchcard.models.Shift;
import com.codepath.punchcard.models.User;

import java.util.ArrayList;
import java.util.Date;


public class UpcomingShiftListFragment extends BaseShiftListFragment {

    public static UpcomingShiftListFragment newInstance(User user) {
        UpcomingShiftListFragment fragment = new UpcomingShiftListFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    //Todo: Refactor logic into base class and remodel
    @Override
    protected void filterShifts(ArrayList<Shift> shifts) {
        Date d = new Date();
        for (Shift s : shifts) {
            if (d.before(s.getStartTime())) {
                this.aShifts.add(s);
            }
        }
    }
}
