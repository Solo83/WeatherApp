package com.solo83.weatherapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solo83.weatherapp.dto.GetLocationRequest;
import com.solo83.weatherapp.utils.config.PropsUtil;
import com.solo83.weatherapp.utils.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.net.URIBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j

public final class OpenWeatherApiService {

    private static final String API_KEY = PropsUtil.GetOpenWeatherApiKey();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static HttpClient client = HttpClient.newHttpClient();
    private static OpenWeatherApiService INSTANCE;

    public OpenWeatherApiService(HttpClient httpClient) {
        client = httpClient;
    }

    private OpenWeatherApiService() {
    }

    public static synchronized OpenWeatherApiService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OpenWeatherApiService();
        }
        return INSTANCE;
    }

    public List<GetLocationRequest> getLocations(String locationName) throws ServiceException {
        List<GetLocationRequest> locations;
        try {
            String json = geocodingApiRequest(locationName);
            JsonNode geoNode = objectMapper.readTree(json);
            log.info("Geocoding api response: {}", geoNode);
            locations = new ArrayList<>();
            for (JsonNode node : geoNode) {
                GetLocationRequest location = new GetLocationRequest();
                location.setName(node.get("name").asText());
                location.setLatitude(node.get("lat").decimalValue());
                location.setLongitude(node.get("lon").decimalValue());
                if (node.has("state")) {
                    location.setState(node.get("state").asText());
                } else {
                    location.setState("");
                }
                location.setCountry(node.get("country").asText());
                locations.add(location);
            }
            for (GetLocationRequest location : locations) {
                String dataJson = currentWeatherDataApiRequest(location.getLatitude().toString(), location.getLongitude().toString());
                log.info("Data api response: {}", dataJson);
                JsonNode dataNode = objectMapper.readTree(dataJson);
                JsonNode mainNode = dataNode.get("main");
                String temp = mainNode.get("temp").asText();
                location.setTemperature(temp);
            }
        } catch (Exception e) {
            throw new ServiceException("Error while getting locations");
        }
        return locations;
    }

    public Optional<GetLocationRequest> updateLocationData(GetLocationRequest getLocationRequest) throws ServiceException {
        Optional<GetLocationRequest> location;
        String latitude = getLocationRequest.getLatitude().toString();
        String longitude = getLocationRequest.getLongitude().toString();
        try {
            String dataJson = currentWeatherDataApiRequest(latitude, longitude);
            log.info("Geocoding api response: {}", dataJson);
            JsonNode dataNode = objectMapper.readTree(dataJson);
            JsonNode mainNode = dataNode.get("main");
            JsonNode sysNode = dataNode.get("sys");
            String temp = mainNode.get("temp").asText();
            getLocationRequest.setCountry(sysNode.get("country").asText());
            getLocationRequest.setTemperature(temp);
            location = Optional.of(getLocationRequest);
        } catch (Exception e) {
            throw new ServiceException("Error while updating temperature");
        }
        return location;
    }

    private String sendApiRequest(URI uri) throws Exception {
        log.info("Sending request to URI: {}", uri);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private String geocodingApiRequest(String cityName) throws Exception {
        URI uri = new URIBuilder("https://api.openweathermap.org/geo/1.0/direct").addParameter("q", cityName).addParameter("limit", "5").addParameter("appid", API_KEY).build();
        return sendApiRequest(uri);
    }

    private String currentWeatherDataApiRequest(String latitude, String longitude) throws Exception {
        URI uri = new URIBuilder("https://api.openweathermap.org/data/2.5/weather").addParameter("lat", latitude).addParameter("lon", longitude).addParameter("units", "metric").addParameter("appid", API_KEY).build();
        return sendApiRequest(uri);
    }

}
