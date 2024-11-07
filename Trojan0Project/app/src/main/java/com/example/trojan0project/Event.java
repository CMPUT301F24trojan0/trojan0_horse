package com.example.trojan0project;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class Event implements Serializable {
    private String eventName;
    private Bitmap qrCodeBitmap;



    //constructor

    public Event(String eventName, Bitmap qrCodeBitmap){
        this.eventName = eventName;
        this.qrCodeBitmap = qrCodeBitmap;

    }

    // getters

    public String getEventName(){
        return eventName;
    }
    public Bitmap getQrCodeBitmap() {
        return qrCodeBitmap;
    }




}
