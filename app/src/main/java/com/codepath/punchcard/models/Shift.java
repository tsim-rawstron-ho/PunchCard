package com.codepath.punchcard.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import java.util.Date;

/**
 * Created by harris on 3/7/15.
 */

@ParseClassName("Shift")
public class Shift extends ParseObject {
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
}
