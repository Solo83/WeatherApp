package com.solo83.weatherapp.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class GetLocationRequest {
    private Integer id;
    private String name;
    private String country;
    private String state;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String temperature;

    public GetLocationRequest(String name, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GetLocationRequest() {
    }
}


