package com.codepath.punchcard.models;

import android.util.Log;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Company")
public class Company extends ParseObject {
    public interface CompanyListener {
        public void shiftsFetched(List<Shift> shifts);
    }

    private static final String NAME = "name";
    private static final String ADDRESS = "address";

    public void setName(String name) {
        put(NAME, name);
    }

    public void setAddress(String address) { put(ADDRESS, address); }
    
    public String getName() {
        return getString(NAME);
    }

    public String getAddress() { return getString(ADDRESS); }

    public void getShifts(Date selectedDate, final CompanyListener listener) {
        ParseQuery<Shift> query = ParseQuery.getQuery(Shift.class);
        query.orderByDescending("startTime");
        query.whereGreaterThanOrEqualTo("startTime", selectedDate);
        Date endOfDate = (Date) selectedDate.clone();
        endOfDate.setHours(23);
        endOfDate.setMinutes(55);
        query.whereLessThan("startTime", endOfDate);
        query.whereEqualTo("company", this);
        query.findInBackground(new FindCallback<Shift>() {
            public void done(List<Shift> shifts, ParseException e) {
                if (e == null) {
                    listener.shiftsFetched(shifts);
                } else {
                    Log.d("message", "Error: " + e.getMessage());
                }
            }
        });
    }

}
