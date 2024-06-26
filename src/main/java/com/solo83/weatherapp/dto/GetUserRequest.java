package com.solo83.weatherapp.dto;

import lombok.Data;

@Data
public class GetUserRequest {
    private String login;
    private String password;
    private String sessionId;
}
