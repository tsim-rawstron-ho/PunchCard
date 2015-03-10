package com.codepath.punchcard.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Company")
public class Company extends ParseObject {

    private static final String NAME = "name";

    public void setName(String name) {
        put(NAME, name);
    }
    
    public String getName() {
        return getString(NAME);
    }
}
