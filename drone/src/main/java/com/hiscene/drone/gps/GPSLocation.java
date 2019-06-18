package com.hiscene.drone.gps;

/**
 * Created by hujun on 2019/4/16.
 */

public class GPSLocation {
    private double longitude = 0;
    private double latitude = 0;
    private double altitude = 0;

    public GPSLocation(double longitude, double latitude, double altitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    public GPSLocation(String str) {
        fromString(str);
    }

    public GPSLocation() {
    }

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

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    @Override
    public String toString() {
        return "gps=" + longitude + "," + latitude + "," + altitude;
    }

    public String getGPS() {
        return longitude + "," + latitude + "," + altitude;
    }

    private void fromString(String str) {
        String arr[] = str.split(",");
        if (arr.length == 3) {
            longitude = Double.parseDouble(arr[0]);
            latitude = Double.parseDouble(arr[1]);
            altitude = Double.parseDouble(arr[2]);
        }
    }

}
