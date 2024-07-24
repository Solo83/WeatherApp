package com.solo83.weatherapp.service;

import com.solo83.weatherapp.dto.LocationFromRequest;
import com.solo83.weatherapp.entity.Location;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.repository.LocationRepository;
import com.solo83.weatherapp.utils.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class LocationService {

    LocationRepository locationRepository;
    OpenWeatherApiService openWeatherApiService;
    UserService userService;

    private static LocationService INSTANCE;

    private LocationService(UserService userService, LocationRepository locationRepository, OpenWeatherApiService openWeatherApiService) {
        this.userService = userService;
        this.locationRepository = locationRepository;
        this.openWeatherApiService = openWeatherApiService;

    }

    public static LocationService getInstance(UserService userService, LocationRepository locationRepository, OpenWeatherApiService openWeatherApiService) {
        if (INSTANCE == null) {
            INSTANCE = new LocationService(userService, locationRepository, openWeatherApiService);
        }
        return INSTANCE;
    }

    public void addLocation(LocationFromRequest locationFromRequest, User user) {

        Optional<Location> location =
                getLocation(locationFromRequest.getLatitude(), locationFromRequest.getLongitude()
                );

        location.ifPresentOrElse(
                loc -> {
                    if (user.getLocations().contains(loc)) {
                        throw new ServiceException("You already have this location");
                    }
                    user.addLocation(loc);
                    userService.update(user);
                },
                () -> {
                    Location addedLocation = new Location();
                    addedLocation.setLatitude(locationFromRequest.getLatitude());
                    addedLocation.setLongitude(locationFromRequest.getLongitude());
                    addedLocation.setName(locationFromRequest.getName());
                    user.addLocation(addedLocation);
                    userService.update(user);
                }
        );
    }

    public List<Location> getLocations(Integer userId) {
        try {
            return locationRepository.findByUserID(userId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceException("Error while retrieving user locations");
        }
    }

    public Optional<Location> getLocation(Double latitude, Double longitude) {
        return locationRepository.getLocation(latitude, longitude);
    }


    public List<LocationFromRequest> getUpdatedLocation(List<Location> locations) {
        List<LocationFromRequest> locationRequests = new ArrayList<>();
        for (Location location : locations) {
            Integer id = location.getId();
            String name = location.getName();
            Double longitude = location.getLongitude();
            Double latitude = location.getLatitude();
            LocationFromRequest locationFromRequest = new LocationFromRequest();
            locationFromRequest.setId(id);
            locationFromRequest.setName(name);
            locationFromRequest.setLongitude(longitude);
            locationFromRequest.setLatitude(latitude);
            Optional<LocationFromRequest> updatedLocation;
            try {
                updatedLocation = openWeatherApiService.updateLocationData(locationFromRequest);
            } catch (ServiceException e) {
                log.error(e.getMessage());
                throw new ServiceException("Error while updating locations");
            }
            updatedLocation.ifPresent(locationRequests::add);
        }
        return locationRequests;
    }

    public void removeLocation(Integer id, User user) {
        Optional<Location> location = locationRepository.get(id);
        user.removeLocation(location.get());
        userService.update(user);
    }
}


