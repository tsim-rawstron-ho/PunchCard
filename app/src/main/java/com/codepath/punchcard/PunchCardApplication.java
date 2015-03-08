package com.codepath.punchcard;

import android.app.Application;

import com.parse.Parse;


public class PunchCardApplication extends Application {
    public static final String APPLICATION_ID = "4VYyIOXcu48JChNmqMfLnO5fcIs7jaqOLDPsrqs5";
    public static final String CLIENT_KEY = "BLWJSFB7acKWFGV10qS6lK3XllAo5HevlwiiHGB0";
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
    }
}
