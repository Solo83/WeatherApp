package com.solo83.weatherapp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LocationFromRequest {
    private Integer id;
    private String name;
    private String country;
    private String state;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String temperature;

    public LocationFromRequest(String name, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationFromRequest() {
    }
}


