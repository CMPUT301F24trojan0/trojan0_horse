/**
 * Purpose:
 * This class represents an Organizer object with a facility name and a list of events.
 *
 * Design Rationale:
 * Provides various constructors for setting and getting organizer related information.
 *
 * Outstanding Issues:
 * No Issues.
 */
package com.example.trojan0project;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Organizer implements Parcelable {
    private String facilityName;
    private List<Event> events;

    // Default constructor for Firebase
    public Organizer() {
        this.events = new ArrayList<>();
    }

    public Organizer(String facilityName, List<Event> events) {
        this.facilityName = facilityName;
        this.events = events != null ? events : new ArrayList<>();
    }

    // Getters and Setters
    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    // Parcelable implementation
    protected Organizer(Parcel in) {
        facilityName = in.readString();
        events = new ArrayList<>();
        in.readList(events, Event.class.getClassLoader());
    }

    public static final Creator<Organizer> CREATOR = new Creator<Organizer>() {
        @Override
        public Organizer createFromParcel(Parcel in) {
            return new Organizer(in);
        }

        @Override
        public Organizer[] newArray(int size) {
            return new Organizer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(facilityName);
        dest.writeList(events);
    }
}
