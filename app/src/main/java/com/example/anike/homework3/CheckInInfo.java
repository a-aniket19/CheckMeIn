package com.example.anike.homework3;

/**
 * Created by anike on 05-11-2018.
 */

public class CheckInInfo {
    double latitude, longitude;
    String address, date;

    public CheckInInfo(double latitude, double longitude, String address, String date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
