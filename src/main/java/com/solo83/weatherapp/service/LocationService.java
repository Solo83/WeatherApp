package com.solo83.weatherapp.service;

import com.solo83.weatherapp.dto.GetLocationRequest;
import com.solo83.weatherapp.entity.Location;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.repository.LocationRepository;
import com.solo83.weatherapp.utils.exception.RepositoryException;
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

    public void addLocation(GetLocationRequest getLocationRequest, User user) {
            Location location = new Location();
            location.setLatitude(getLocationRequest.getLatitude());
            location.setLongitude(getLocationRequest.getLongitude());
            location.setName(getLocationRequest.getName());
            location.setUser(user);
            log.info("Add location : {}", location);
            try {
            locationRepository.save(location);
        } catch (RepositoryException e) {
            log.error("Location already exist in DB");
        }
    }

    public List<Location> getLocations(Integer userId) throws RepositoryException {
        try {
            return locationRepository.findByUserID(userId);
        } catch (RepositoryException e) {
            throw new RepositoryException("Error while getting location");
        }
    }

    public List<GetLocationRequest> getUpdatedLocation(List<Location> locations) throws ServiceException {
        List<GetLocationRequest> locationRequests = new ArrayList<>();
        for (Location location : locations) {
            Integer id = location.getId();
            String name = location.getName();
            BigDecimal longitude = location.getLongitude();
            BigDecimal latitude = location.getLatitude();
            GetLocationRequest getLocationRequest = new GetLocationRequest();
            getLocationRequest.setId(id);
            getLocationRequest.setName(name);
            getLocationRequest.setLongitude(longitude);
            getLocationRequest.setLatitude(latitude);

            Optional<GetLocationRequest> updatedLocation = openWeatherApiService.updateLocationData(getLocationRequest);
            updatedLocation.ifPresent(locationRequests::add);

        }
        return locationRequests;
        }

    public void removeLocation(Integer id) {
        try {
            locationRepository.delete(id);
        } catch (RepositoryException e) {
            log.error("Location does not exist in DB");
        }
    }
}


