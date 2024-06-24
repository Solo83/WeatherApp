package com.solo83.weatherapp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GetLocationRequest {
    private String name;
    private String country;
    private String state;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String temperature;
}
