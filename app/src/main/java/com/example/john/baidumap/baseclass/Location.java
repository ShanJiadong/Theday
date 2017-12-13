package com.example.john.baidumap.baseclass;

/**
 * Created by android学习 on 2017/12/12.
 */

public class Location {
    private double longitude;
    private double latitude;
    private String address;
    private String city;

    public Location(){
        address = "";
        city = "珠海";
        longitude = 0;
        latitude = 0;
    }

    public Location(double longitude, double latitude, String address, String city){
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.city = city;
    }

    public double getLongitude(){
        return longitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public String getAddress(){
        return address;
    }

    public String getCity(){
        return city;
    }
}
