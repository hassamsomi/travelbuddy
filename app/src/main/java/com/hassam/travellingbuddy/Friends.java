package com.hassam.travellingbuddy;

public class Friends {
    public String date;
    public String name;
    public String image;
    public String online;


    public Friends(){

    }

    public Friends(String date) {

        this.date = date;
        this.name = name;
        this.image = image;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
