package com.example.berkin1.myapplication.Pods;

public class Poditem {
    private String name;
    private final String address;
    private String photoId;

    public Poditem(String name,String address,  String photoId) {
        this.name = name;
        this.address = address;
        this.photoId = photoId;

    }

    public String getPhotoId() {
        return photoId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {return address;}

}