package com.solo83.weatherapp.utils.config;
import lombok.extern.slf4j.Slf4j;
import java.util.Properties;

@Slf4j
public class PropsUtil {

    public static String GetOpenWeatherApiKey() {

        Properties properties = new Properties();
        try {
            properties.load(PropsUtil.class.getClassLoader().getResourceAsStream("api.key"));
        }
        catch (Exception e) {
            log.error("Error loading api key", e);
        }
        return properties.getProperty("apiKey");
    }
}
