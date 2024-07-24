package com.solo83.weatherapp.dto;

import lombok.Data;

@Data
public class LocationFromRequest {
    private Integer id;
    private String name;
    private String country;
    private String state;
    private Double latitude;
    private Double longitude;
    private String temperature;

    public LocationFromRequest(String name, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationFromRequest() {
    }
}


