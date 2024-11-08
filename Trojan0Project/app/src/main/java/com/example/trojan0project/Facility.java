package com.example.trojan0project;

import java.io.Serializable;

public class Facility implements Serializable {
    private String facilityName;
    //constructor

    public Facility(String eventName){
        this.facilityName = eventName;
    }

    // getters

    public String getFacilityName(){
        return facilityName;
    }


}
