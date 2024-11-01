package com.example.trojan0project;

public class Event {
    private String name;
    private double latitude;
    private double longitude;
    private String posterPath;

    public Event(String name, double latitude, double longitude, String posterPath) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.posterPath = posterPath;
    }

    // Getters and setters
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
}
