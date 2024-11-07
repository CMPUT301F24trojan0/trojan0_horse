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
  //CITE
    public String getQrCodeBase64() {
        if (qrCodeBitmap == null) {
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }



}
