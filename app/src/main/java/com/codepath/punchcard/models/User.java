package com.codepath.punchcard.models;

import com.parse.ParseUser;

/**
 * Created by harris on 3/8/15.
 */
public class User extends ParseUser {
  public User() {
    super();
  }

  public String getName() {
    return getString("name");
  }

  public void setName(String name) {
    put("name", name);
  }
}
