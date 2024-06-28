package com.solo83.weatherapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetUserRequest {
    private String login;
    private String password;
}
