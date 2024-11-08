package com.example.trojan0project;

import android.os.Parcel;
import android.os.Parcelable;
import android.graphics.Bitmap;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class Event implements Parcelable, Serializable{
    private Bitmap qrCodeBitmap;
    private String eventId;
    private String eventName;
    private double latitude;
    private double longitude;
    private String posterPath;
    private String qrCodeUrl;    // URL for the QR code image
    private String description;  // Event description
    private String time;         // Event time

    // Default constructor for Firestore
    public Event() {}

    // Constructor
    public Event(String eventName, String eventId, double latitude, double longitude, String posterPath) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.posterPath = posterPath;
    }

    public Event(String eventName, Bitmap qrCodeBitmap){
        this.eventName = eventName;
        this.qrCodeBitmap = qrCodeBitmap;
    }

    // Parcelable constructor
    protected Event(Parcel in) {
        eventId = in.readString();
        eventName = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        posterPath = in.readString();
        qrCodeUrl = in.readString();
    }

    // Creator for Parcelable
    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventId);
        dest.writeString(eventName);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(posterPath);
        dest.writeString(qrCodeUrl);
    }

    // Getters and setters
    public String getEventId() {return eventId;}

    public void setEventId(String id) {
        this.eventId = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String name) {
        this.eventName = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public Bitmap getQrCodeBitmap() {
        return qrCodeBitmap;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
