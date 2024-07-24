package com.solo83.weatherapp.service;

import com.solo83.weatherapp.dto.LocationFromRequest;
import com.solo83.weatherapp.utils.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenWeatherApiServiceTest {

    @InjectMocks
    private OpenWeatherApiService openWeatherApiService;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<Object> httpResponse;

    @Test
    public void testGetLocations() throws Exception {
        String cityName = "London";
        String geocodingResponse = "[{\"name\":\"London\",\"lat\":51.5074,\"lon\":-0.1278,\"country\":\"GB\"}]";
        String weatherResponse = "{\"main\":{\"temp\":15.0},\"sys\":{\"country\":\"GB\"}}";

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        when(httpResponse.body()).thenReturn(geocodingResponse).thenReturn(weatherResponse);

        List<LocationFromRequest> locations = openWeatherApiService.getLocations(cityName);

        assertEquals(1, locations.size());
        assertEquals("London", locations.get(0).getName());
        assertEquals("15.0", locations.get(0).getTemperature());
    }

    @Test
    public void testGetLocationsThrowsException() throws Exception {
        String cityName = "InvalidCity";
        String errorMessage = "Error while getting locations";

        when(httpClient.send(any(),any())).thenReturn(httpResponse).thenThrow(new RuntimeException());

        ServiceException exception = assertThrows(ServiceException.class, () -> openWeatherApiService.getLocations(cityName));
        assertEquals(errorMessage, exception.getMessage());
    }

   @Test
    public void testUpdateLocationData() throws Exception {
        LocationFromRequest locationRequest = new LocationFromRequest();
        locationRequest.setLatitude(51.5074);
        locationRequest.setLongitude(-0.1278);
        String weatherResponse = "{\"main\":{\"temp\":15.0},\"sys\":{\"country\":\"GB\"}}";

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(weatherResponse);

        Optional<LocationFromRequest> updatedLocation = openWeatherApiService.updateLocationData(locationRequest);

        assertTrue(updatedLocation.isPresent());
        assertEquals("15.0", updatedLocation.get().getTemperature());

    }

    @Test
    public void testUpdateLocationDataThrowsException() throws Exception {
        LocationFromRequest locationRequest = new LocationFromRequest();
        locationRequest.setLatitude(51.5074);
        locationRequest.setLongitude(-0.1278);
        String errorMessage = "Error while updating temperature";

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(new RuntimeException());

        ServiceException exception = assertThrows(ServiceException.class, () -> openWeatherApiService.updateLocationData(locationRequest));
        assertEquals(errorMessage, exception.getMessage());

    }

    @Test
    public void testApi4xxErrorHandling() throws Exception {
        String cityName = "London";

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException());

        ServiceException exception = assertThrows(ServiceException.class, () -> openWeatherApiService.getLocations(cityName));
        assertEquals("Error while getting locations", exception.getMessage());

    }

    @Test
    public void testApi5xxErrorHandling() throws Exception {
        LocationFromRequest locationRequest = new LocationFromRequest();
        locationRequest.setLatitude(51.5074);
        locationRequest.setLongitude(-0.1278);

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException());

        ServiceException exception = assertThrows(ServiceException.class, () -> openWeatherApiService.updateLocationData(locationRequest));
        assertEquals("Error while updating temperature", exception.getMessage());

    }
}