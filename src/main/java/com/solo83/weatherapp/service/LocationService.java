package com.solo83.weatherapp.service;

import com.solo83.weatherapp.dto.LocationFromRequest;
import com.solo83.weatherapp.entity.Location;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.repository.LocationRepository;
import com.solo83.weatherapp.utils.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class LocationService {

    LocationRepository locationRepository;
    OpenWeatherApiService openWeatherApiService;

    private static LocationService INSTANCE;

        private LocationService(LocationRepository locationRepository, OpenWeatherApiService openWeatherApiService) {
            this.locationRepository = locationRepository;
            this.openWeatherApiService = openWeatherApiService;
        }

        public static LocationService getInstance(LocationRepository locationRepository, OpenWeatherApiService openWeatherApiService) {
            if(INSTANCE == null) {
                INSTANCE = new LocationService(locationRepository, openWeatherApiService);
            }
            return INSTANCE;
        }

    public void addLocation(LocationFromRequest locationFromRequest, User user) {
            Location location = new Location();
            location.setLatitude(locationFromRequest.getLatitude());
            location.setLongitude(locationFromRequest.getLongitude());
            location.setName(locationFromRequest.getName());
            location.setUser(user);
            log.info("Add location : {}", location);
            try {
            locationRepository.save(location);
        } catch (Exception e) {
            log.error("Location already exist in DB");
            throw new ServiceException("You already have this location");
        }
    }

    public List<Location> getLocations(Integer userId) {
        try {
            return locationRepository.findByUserID(userId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceException("Error while retrieving user locations");
        }
    }

    public List<LocationFromRequest> getUpdatedLocation(List<Location> locations) {
        List<LocationFromRequest> locationRequests = new ArrayList<>();
        for (Location location : locations) {
            Integer id = location.getId();
            String name = location.getName();
            BigDecimal longitude = location.getLongitude();
            BigDecimal latitude = location.getLatitude();
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

    public void removeLocation(Integer id) {
        try {
            locationRepository.delete(id);
        } catch (Exception e) {
            log.error("Location does not exist in DB");
        }
    }
}


