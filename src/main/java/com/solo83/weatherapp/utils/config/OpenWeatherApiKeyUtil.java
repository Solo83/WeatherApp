package com.solo83.weatherapp.utils.config;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@Slf4j
@UtilityClass
public class OpenWeatherApiKeyUtil {

    public String GetOpenWeatherApiKey() {
        Properties properties = new Properties();
        try {
            properties.load(OpenWeatherApiKeyUtil.class.getClassLoader().getResourceAsStream("api.key"));
        } catch (Exception e) {
            log.error("Error loading api key", e);
        }
        return properties.getProperty("apiKey");
    }
}
