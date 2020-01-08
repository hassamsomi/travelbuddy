package com.hassam.travellingbuddy;

public class Messages
{
    public String message,type;
    long time;
    boolean seen;

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