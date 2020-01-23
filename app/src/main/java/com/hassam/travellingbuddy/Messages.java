package com.hassam.travellingbuddy;

import com.google.firebase.database.DatabaseReference;

public class Messages
{
    public String message,type;
    public DatabaseReference mUserDatabase;
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


    public Messages(String pMessage, String pType, long pTime, boolean pSeen) {
        message = pMessage;
        type = pType;
        time = pTime;
        seen = pSeen;
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