package com.codepath.punchcard.models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by harris on 3/8/15.
 */
public class User extends ParseUser implements Serializable {
  public static final String PROFILE_IMAGE = "profileImage";
  private final List<Shift> shifts;
  public User() {
    super();
    shifts = new ArrayList<Shift>();
  }

  private boolean picked = false;

  public boolean isPicked() {
    return picked;
  }

  public void setPicked(boolean picked) {
    this.picked = picked;
  }

  public String getFirstName() {
      return getString("firstName");
  }

  public String getLastName() {
      return getString("lastName");
  }
  
  public String getRole() {
    return getString("role");
  }
  
  public boolean canCreateShift() { 
    return getRole().equals("manager");
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

  public String getName() {
    return getFirstName() + " " + getLastName();
  }
}
