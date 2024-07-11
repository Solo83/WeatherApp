package com.solo83.weatherapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class GetUserRequest {
    private Integer id;
    @NonNull
    private String login;
    @NonNull
    private String password;



}
