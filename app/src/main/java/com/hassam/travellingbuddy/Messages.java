package com.hassam.travellingbuddy;

import com.google.firebase.database.DatabaseReference;

public class Messages
{
    public String key;
    public String message,type;
    public DatabaseReference mUserDatabase;
    double longitude,latitude;
    long time;
    boolean seen;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    String from;

    public Messages(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    String to;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Messages(String pMessage, String pType, long pTime, boolean pSeen, double pLatitude, double pLongitude)
    {
        message = pMessage;
        type = pType;
        time = pTime;
        seen = pSeen;
        latitude = pLatitude;
        longitude = pLongitude;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String pMessage) {
        message = pMessage;
    }

    public String getType() {
        return type;
    }

    public void setType(String pType) {
        type = pType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long pTime) {
        time = pTime;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean pSeen) {
        seen = pSeen;
    }
    public Messages(){

    }
}