package com.example.trojan0project;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    private String id;           // Unique event ID
    private String name;
    private double latitude;
    private double longitude;
    private String posterPath;
    private String qrCodeUrl;    // URL for the QR code image

    // Default constructor for Firestore
    public Event() {}

    // Constructor
    public Event(String name, double latitude, double longitude, String posterPath) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.posterPath = posterPath;
    }

    // Parcelable constructor
    protected Event(Parcel in) {
        id = in.readString();
        name = in.readString();
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
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(posterPath);
        dest.writeString(qrCodeUrl);
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
}
