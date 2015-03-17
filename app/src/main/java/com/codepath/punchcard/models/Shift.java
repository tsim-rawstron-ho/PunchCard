package com.codepath.punchcard.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by harris on 3/7/15.
 */

@ParseClassName("Shift")
public class Shift extends ParseObject {

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
}
