package com.codepath.punchcard.models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harris on 3/8/15.
 */
@ParseClassName("User")
public class User extends ParseObject {
  private final List<Shift> shifts;
  public User() {
    super();
    shifts = new ArrayList<Shift>();
    getShifts();
  }

  public String getName() {
    return getString("name");
  }

  public void getShifts() {
    ParseQuery<UsersShift> query = ParseQuery.getQuery(UsersShift.class);
    query.orderByAscending("startTime");
    query.whereEqualTo("user", this);
    query.findInBackground(new FindCallback<UsersShift>() {
      public void done(List<UsersShift> usersShifts, ParseException e) {
        if (e == null) {
          shifts.clear();
          for (UsersShift usersShift : usersShifts) {
            shifts.add(usersShift.getShift());
          }
        } else {
          Log.d("message", "Error: " + e.getMessage());
        }
      }
    });
  }

  public void setName(String name) {
    put("name", name);
  }
}
