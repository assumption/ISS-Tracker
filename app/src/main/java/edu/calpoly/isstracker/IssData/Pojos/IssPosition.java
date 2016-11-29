package edu.calpoly.isstracker.IssData.Pojos;

/*
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IssPosition {

    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;

    */
/**
     *
     * @return
     * The latitude
     *//*

    public Double getLatitude() {
        return latitude;
    }

    */
/**
     *
     * @param latitude
     * The latitude
     *//*

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    */
/**
     *
     * @return
     * The longitude
     *//*

    public Double getLongitude() {
        return longitude;
    }

    */
/**
     *
     * @param longitude
     * The longitude
     *//*

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}*/

import java.util.HashMap;
import java.util.Map;

public class IssPosition {

    private String name;
    private Integer id;
    private float latitude;
    private float longitude;
    private float altitude;
    private float velocity;
    private String visibility;
    private float footprint;
    private int timestamp;
    private float daynum;
    private float solarLat;
    private float solarLon;
    private String units;
    private Map<String, Object> additionalProperties = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public float getFootprint() {
        return footprint;
    }

    public void setFootprint(float footprint) {
        this.footprint = footprint;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public float getDaynum() {
        return daynum;
    }

    public void setDaynum(float daynum) {
        this.daynum = daynum;
    }

    public float getSolarLat() {
        return solarLat;
    }

    public void setSolarLat(float solarLat) {
        this.solarLat = solarLat;
    }

    public float getSolarLon() {
        return solarLon;
    }

    public void setSolarLon(float solarLon) {
        this.solarLon = solarLon;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
