package com.codepath.punchcard;

import android.app.Application;

import com.codepath.punchcard.helpers.FixtureHelper;
import com.codepath.punchcard.models.Company;
import com.codepath.punchcard.models.Shift;
import com.codepath.punchcard.models.ShiftSession;
import com.codepath.punchcard.models.User;
import com.codepath.punchcard.models.UsersShift;
import com.parse.Parse;
import com.parse.ParseObject;

public class PunchCardApplication extends Application {
  public static final String APPLICATION_ID = "4VYyIOXcu48JChNmqMfLnO5fcIs7jaqOLDPsrqs5";
  public static final String CLIENT_KEY = "BLWJSFB7acKWFGV10qS6lK3XllAo5HevlwiiHGB0";
  @Override
  public void onCreate() {
    super.onCreate();
    Parse.enableLocalDatastore(this);
    ParseObject.registerSubclass(User.class);
    ParseObject.registerSubclass(Shift.class);
    ParseObject.registerSubclass(UsersShift.class);
    ParseObject.registerSubclass(Company.class);
    ParseObject.registerSubclass(ShiftSession.class);
    Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
    FixtureHelper.setupFixtureData();
  }
}
