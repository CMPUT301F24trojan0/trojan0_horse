package com.example.trojan0project;

public class Profile {
    private String name;
    private int profileImage;
    public Profile(String name, int profileImage){
        this.name = name;
        this.profileImage = profileImage;
    }

    public String getName(){
        return name;

    }
    public void setName(String name){
        this.name = name;
    }
    public int getProfileImage(){
        return profileImage;

    }
    public void setProfileImage(int profileImage){
        this.profileImage = profileImage;
    }



}
