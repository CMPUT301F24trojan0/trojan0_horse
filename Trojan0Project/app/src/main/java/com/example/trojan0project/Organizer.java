package com.example.trojan0project;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Organizer implements Parcelable {
    private String facilityName;
    private List<Event> events;

    /**
     * Default constructor for Firebase. Initializes an empty list of events.
     */
    public Organizer() {
        this.events = new ArrayList<>();
    }
    /**
     * Constructs an Organizer with the specified facility name and events list.
     *
     * @param facilityName The name of the facility associated with this organizer.
     * @param events       The list of events managed by this organizer. If null, initializes an empty list.
     */
    public Organizer(String facilityName, List<Event> events) {
        this.facilityName = facilityName;
        this.events = events != null ? events : new ArrayList<>();
    }

    /**
     * Gets the facility name associated with this organizer.
     *
     * @return The facility name.
     */
    public String getFacilityName() {
        return facilityName;
    }
    /**
     * Sets the facility name for this organizer.
     *
     * @param facilityName The name of the facility.
     */
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
    /**
     * Gets the list of events managed by this organizer.
     *
     * @return The list of events.
     */
    public List<Event> getEvents() {
        return events;
    }
    /**
     * Sets the list of events managed by this organizer.
     *
     * @param events The list of events.
     */
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /**
     * Constructor for creating an Organizer from a Parcel.
     *
     * @param in The Parcel containing the Organizer's data.
     */
    protected Organizer(Parcel in) {
        facilityName = in.readString();
        events = new ArrayList<>();
        in.readList(events, Event.class.getClassLoader());
    }
    /**
     * Creator for Parcelable, enabling the Organizer class to be passed between activities.
     */
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
    /**
     * Describes the contents of the parcelable Organizer, returning 0 as no special objects are present.
     *
     * @return An integer representing contents.
     */
    @Override
    public int describeContents() {
        return 0;
    }
    /**
     * Writes the Organizer data to a Parcel, enabling Parcelable support.
     *
     * @param dest  The destination Parcel.
     * @param flags Additional flags (none used).
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(facilityName);
        dest.writeList(events);
    }
}
