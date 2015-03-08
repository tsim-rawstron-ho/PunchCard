package com.codepath.punchcard.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by harris on 3/8/15.
 */
@ParseClassName("UsersShift")
public class UsersShift extends ParseObject {
  public void setUser(User user) {
    put("user", user);
  }
  public void setShift(Shift shift) {
    put("shift", shift);
  }

  public Shift getShift() {
    return (Shift) get("shift");
  }
}
