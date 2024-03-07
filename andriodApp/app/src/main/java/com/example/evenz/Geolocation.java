package com.example.evenz;

public class Geolocation {
    private String geolocationID;
    private float xcoord;
    private float ycoord;

    // Constructor
    public Geolocation(String geolocationID, float xcoord, float ycoord) {
        this.geolocationID = geolocationID;
        this.xcoord = xcoord;
        this.ycoord = ycoord;
    }
}
