package com.codepath.punchcard.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Company")
public class Company extends ParseObject {

    private static final String NAME = "name";
    private static final String ADDRESS = "address";

    public void setName(String name) {
        put(NAME, name);
    }

    public void setAddress(String address) { put(ADDRESS, address); }
    
    public String getName() {
        return getString(NAME);
    }

    public String getAddress() { return getString(ADDRESS); }
}
