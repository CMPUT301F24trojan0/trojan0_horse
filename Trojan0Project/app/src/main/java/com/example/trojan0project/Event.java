/**
 * Purpose:
 * The class represents an event in the application, storing details such as event ID, name,
 * location, poster, time and QR code. It implements `Parcelable` and `Serializable` interfaces.
 *
 * Design Rationale:
 * Contains constructors and setters to initialize event data from various sources like Firestore or QR generation.
 *
 * Outstanding Issues:
 * No issues
 */
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
    private int waitlistLimit;   // Event waitlist limit

    // Default constructor for Firestore
    public Event() {}

    /**
     * Constructor to create an Event object with essential details.
     *
     * @param eventName Name of the event.
     * @param eventId   Unique identifier for the event.
     * @param latitude  Latitude of the event location.
     * @param longitude Longitude of the event location.
     * @param posterPath Path or URL for the event poster image.
     */
    public Event(String eventName, String eventId, double latitude, double longitude, String posterPath) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.posterPath = posterPath;
        this.waitlistLimit = -1;
    }

    /**
     * Constructor to create an Event object with a name and QR code Bitmap.
     *
     * @param eventName     Name of the event.
     * @param qrCodeBitmap  Bitmap image of the event's QR code.
     */
    public Event(String eventName, Bitmap qrCodeBitmap){
        this.eventName = eventName;
        this.qrCodeBitmap = qrCodeBitmap;
        this.waitlistLimit = -1;
    }

    /**
     * Constructor to create an Event object from a Parcel (for Parcelable).
     *
     * @param in Parcel containing event data.
     */
    protected Event(Parcel in) {
        eventId = in.readString();
        eventName = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        posterPath = in.readString();
        qrCodeUrl = in.readString();
        waitlistLimit = in.readInt();
    }

    /**
     * Generates an instance of the Event class from a Parcel.
     */
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

    /**
     * Writes the event data to a Parcel.
     *
     * @param dest  The Parcel to write data into.
     * @param flags Flags for writing to the Parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventId);
        dest.writeString(eventName);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(posterPath);
        dest.writeString(qrCodeUrl);
        dest.writeInt(waitlistLimit); //Include Waitlist Limit in Parcel

    }

    /**
     * Gets the event ID.
     *
     * @return The event ID.
     */
    public String getEventId() {return eventId;}

    /**
     * Sets the event ID.
     *
     * @param id Unique identifier for the event.
     */
    public void setEventId(String id) {
        this.eventId = id;
    }

    /**
     * Gets the event name.
     *
     * @return The name of the event.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the event name.
     *
     * @param name The name of the event.
     */
    public void setEventName(String name) {
        this.eventName = name;
    }

    /**
     * Gets the event location latitude.
     *
     * @return The latitude of the event location.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the event location.
     *
     * @param latitude Latitude for the event.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the event location longitude.
     *
     * @return The longitude of the event location.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the event location.
     *
     * @param longitude Longitude for the event.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the event poster path.
     *
     * @return Path or URL to the event's poster.
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     * Sets the event poster path or URL.
     *
     * @param posterPath Path or URL of the event's poster image.
     */

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    /**
     * Gets the QR code URL.
     *
     * @return URL to the QR code image.
     */
    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    /**
     * Gets the QR code Bitmap.
     *
     * @return The Bitmap image of the QR code.
     */
    public Bitmap getQrCodeBitmap() {
        return qrCodeBitmap;
    }

    /**
     * Sets the QR code URL.
     *
     * @param qrCodeUrl URL for the event's QR code image.
     */
    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    /**
     * Gets the event description.
     *
     * @return The description of the event.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the event description.
     *
     * @param description Detailed description of the event.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the event time.
     *
     * @return The scheduled time of the event.
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets the event time.
     *
     * @param time The scheduled time of the event.
     */
    public void setTime(String time) {
        this.time = time;
    }


    /**
     * Gets the waitlist limit for the event.
     *
     * @return The waitlist limit, or -1 if no limit is set.
     */
    public int getWaitlistLimit() {
        return waitlistLimit;
    }

    /**
     * Sets the waitlist limit for the event.
     *
     * @param waitlistLimit The maximum number of entrants allowed in the waitlist.
     */
    public void setWaitlistLimit(int waitlistLimit) {
        this.waitlistLimit = waitlistLimit;
    }


}
