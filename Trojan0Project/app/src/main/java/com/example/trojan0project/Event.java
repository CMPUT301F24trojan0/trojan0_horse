package com.example.trojan0project;

import java.io.Serializable;

public class Event implements Serializable {
    private String eventName;
    private int imageId;
    private boolean hasQR;




    //constructor

    public Event(String eventName, int imageId){
        this.eventName = eventName;
        this.imageId = imageId;
        this.hasQR = true;
    }

    // getters

    public String getEventName(){
        return eventName;
    }
    public int getImageResId() {
        return imageId;
    }

    public boolean hasQRCode() {
        return hasQR;
    }
    public void removeQRCode() {
        this.imageId = 0;
        this.hasQR = false;
    }

}
