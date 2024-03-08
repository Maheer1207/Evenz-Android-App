package com.example.evenz;
/**
 * Represents a geographical location with a unique identifier, latitude, and longitude coordinates.
 * Instances of this class are used to store and retrieve location information for events, users, or other entities.
 *
 * <p>The geolocation is defined by a unique identifier ({@code geolocationID}) and corresponding
 * latitude ({@code xcoord}) and longitude ({@code ycoord}) coordinates.
 *
 * @see Event
 * @see User
 */
public class Geolocation {
    private String geolocationID;
    private float xcoord;
    private float ycoord;

    /**
     * Constructs a new Geolocation instance with the specified unique identifier, latitude, and longitude coordinates.
     *
     * @param geolocationID The unique identifier for the geolocation.
     * @param xcoord        The latitude coordinate of the geolocation.
     * @param ycoord        The longitude coordinate of the geolocation.
     */
    public Geolocation(String geolocationID, float xcoord, float ycoord) {
        this.geolocationID = geolocationID;
        this.xcoord = xcoord;
        this.ycoord = ycoord;
    }
}
