package com.example.evenz;

/**
 * a class whose objects store a location in X and Y float coordiantes, and the documentID where
 * it is stored in Firebase.
 */
public class Geolocation {
    private String geolocationID;
    private float xcoord;
    private float ycoord;

    /**
     * constructor, all 3 fields must be injected
     * @param geolocationID firebase ID
     * @param xcoord x coord
     * @param ycoord y coord
     */
    // Constructor
    public Geolocation(String geolocationID, float xcoord, float ycoord) {
        this.geolocationID = geolocationID;
        this.xcoord = xcoord;
        this.ycoord = ycoord;
    }


// Getters and Setters
    public String getGeolocationID() {
        return geolocationID;
    }

    public void setGeolocationID(String geolocationID) {
        this.geolocationID = geolocationID;
    }

    public float getXcoord() {
        return xcoord;
    }

    public void setXcoord(float xcoord) {
        this.xcoord = xcoord;
    }

    public float getYcoord() {
        return ycoord;
    }

    public void setYcoord(float ycoord) {
        this.ycoord = ycoord;
    }
}
