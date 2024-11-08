package com.example.trojan0project;

public class Event {
    private String id;           // Unique event ID
    private String name;
    private double latitude;
    private double longitude;
    private String posterPath;
    private String qrCodeUrl;    // URL for the QR code image
    private String description;  // Event description
    private String time;         // Event time

    // Default constructor for Firestore
    public Event() {}

    // Constructor
    public Event(String name, double latitude, double longitude, String posterPath) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.posterPath = posterPath;
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
