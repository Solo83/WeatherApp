package com.solo83.weatherapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class UserFromRequest {
    private Integer id;
    @NonNull
    private String login;
    @NonNull
    private String password;



}
