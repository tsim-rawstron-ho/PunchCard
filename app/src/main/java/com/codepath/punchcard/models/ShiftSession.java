package com.codepath.punchcard.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("ShiftSession")
public class ShiftSession extends ParseObject {

    public enum SessionType {
        WORK(0),
        BREAK(1);

        private int value;
        private SessionType(int value) {
            this.value = value;
        }
    }
    private static SessionType intToSessionType(int i) {
        switch (i) {
            case 0: return SessionType.WORK;
            case 1: return SessionType.BREAK;
            default: return null;
        }
    }

    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String TYPE = "type";
    private static final String USER_SHIFT = "userShift";

    public void setUserShift(UsersShift userShift) {
        put(USER_SHIFT, userShift);
    }

    public UsersShift getUsersShift() {
        return (UsersShift) getParseObject(USER_SHIFT);
    }

    public void setType(SessionType type) {
        put(TYPE, type.value);
    }

    public SessionType getType() {
        return intToSessionType(getInt(TYPE));
    }

    public void setStartTime(Date startTime) {
        put(START_TIME, startTime);
    }

    public Date getStartTime() {
        return getDate(START_TIME);
    }

    public void setEndTime(Date endTime) {
        put(END_TIME, endTime);
    }
    
    public Date getEndTime() {
        return getDate(END_TIME);
    }
}
