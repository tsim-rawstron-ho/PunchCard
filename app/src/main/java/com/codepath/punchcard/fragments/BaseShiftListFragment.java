package com.codepath.punchcard.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.punchcard.R;
import com.codepath.punchcard.adapters.ShiftAdapter;
import com.codepath.punchcard.models.Shift;

import java.util.ArrayList;

public abstract class BaseShiftListFragment extends Fragment {
    protected ShiftAdapter<Shift> aShifts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aShifts = new ShiftAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<Shift>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shift_list, container, false);
        ListView lvShifts = (ListView) v.findViewById(R.id.lvShifts);
        lvShifts.setAdapter(aShifts);
        populateShifts();
        return v;
    }

    protected abstract void populateShifts();
}
