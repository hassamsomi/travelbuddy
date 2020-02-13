package com.hassam.travellingbuddy;

public class User {
    public String name;
    public String image;
    public String aboutMe;

    public User(){

    }

    public User(String name, String image, String aboutMe) {
        this.name = name;
        this.image = image;
        this.aboutMe = aboutMe;
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

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }
}