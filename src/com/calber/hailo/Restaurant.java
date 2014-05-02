package com.calber.hailo;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by calber on 7/7/13.
 */
public class Restaurant {
    private static final String TAG = "com.calber.hailo";

    private String icon,id,name,price_level,rating,vicinity;
    private double lat,lng;


    public Restaurant(JsonNode node) {
        name = node.get("name").textValue();
        vicinity = node.get("vicinity").textValue();
        icon = node.get("icon").textValue();
        lat = node.get("geometry").get("location").get("lat").asDouble();
        lng = node.get("geometry").get("location").get("lng").asDouble();
    }

    public String getIcon() {
        return icon;
    }

    public String getVicinity() {
        return vicinity;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice_level() {
        return price_level;
    }

    public String getRating() {
        return rating;
    }
}
