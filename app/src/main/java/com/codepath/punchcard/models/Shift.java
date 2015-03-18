package com.codepath.punchcard.models;

import android.util.Log;
import com.codepath.punchcard.helpers.DateHelper;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by harris on 3/7/15.
 */

@ParseClassName("Shift")
public class Shift extends ParseObject {
    public interface ShiftListener {
        public void usersFetched(Shift shift, List<User> users);
    }
    private static final String INSTRUCTION = "instruction";
    private static final String COMPANY = "company";
    
    public Company getCompany() {
        return (Company) getParseObject(COMPANY);
    }
    
    public void setCompany(Company company) {
        put(COMPANY, company);
    }

    public String getInstruction() {
        return getString(INSTRUCTION);
    }
    
    public void setInstruction(String instruction) {
        put(INSTRUCTION, instruction);
    }
    
    public Date getStartTime() {
        return getDate("startTime");
    }

    public Date getEndTime() {
        return getDate("endTime");
    }

    public void setStartTime(Date startTime) {
        put("startTime", startTime);
    }

    public void setEndTime(Date endTime) {
        put("endTime", endTime);
    }

    public void addUser(User user) {
        UsersShift usersShift = new UsersShift();
        usersShift.setShift(this);
        usersShift.setUser(user);
        usersShift.saveInBackground();
    }

    public void getUsers(final ShiftListener listener) {
        final Shift shift = this;
        ParseQuery<UsersShift> query = ParseQuery.getQuery(UsersShift.class);
        query.whereEqualTo("shift", shift);
        query.include("user");
        query.findInBackground(new FindCallback<UsersShift>() {
            public void done(List<UsersShift> usersShifts, ParseException e) {
                if (e == null) {
                    List<User> users = new ArrayList<>();
                    for (UsersShift userShift : usersShifts) {
                        users.add(userShift.getUser());
                    }
                    listener.usersFetched(shift, users);
                } else {
                    Log.d("message", "Error: " + e.getMessage());
                }
            }
        });
    }

    public String getEndTimeString() {
        Date endTime = getEndTime();
        return DateHelper.formateTime(endTime);
    }

    public String getStartTimeString() {
        Date startTime = getStartTime();
        return DateHelper.formateTime(startTime);
    }
}
