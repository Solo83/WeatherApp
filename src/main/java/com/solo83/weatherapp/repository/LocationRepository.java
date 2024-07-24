package com.solo83.weatherapp.repository;

import com.solo83.weatherapp.entity.Location;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

@Slf4j
public class LocationRepository {
    private static LocationRepository INSTANCE;
    private final SessionFactory sessionFactory;

    private LocationRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public static LocationRepository getInstance(SessionFactory sessionFactory) {
        if (INSTANCE == null) {
            INSTANCE = new LocationRepository(sessionFactory);
        }
        return INSTANCE;
    }


    public List<Location> findByUserID(Integer userId) {
        List<Location> findedLocations;
        try (Session session = sessionFactory.openSession()) {
            try {
               // Query<Location> query = session.createQuery("from Location as location where location.user.id = :id", Location.class);
                Query<Location> query = session.createQuery("from Location as location join location.users u where u.id = :id", Location.class);
                query.setParameter("id", userId);
                findedLocations = query.getResultList();
                log.info("Locations size {}", findedLocations);
            } catch (Exception e) {
                log.error("Error while getting Locations by userId");
                 throw new RepositoryException("Error while getting Locations by userId");
            }
            return findedLocations;
        }
    }

    public Optional<Location> getLocation(Double latitude, Double longitude) {
        Optional<Location> location;
        try (Session session = sessionFactory.openSession()) {
            try {
                Query<Location> query = session.createQuery("from Location where latitude = :latitude and longitude = :longitude", Location.class);
                query.setParameter("latitude", latitude);
                query.setParameter("longitude", longitude);
                location = Optional.of(query.getSingleResult());
                log.info("Location found");
            } catch (Exception e) {
                log.error("Error", e);
                return Optional.empty();
            }
            return location;
        }
    }

    public Optional<Location> get(Integer id) {
        Optional<Location> location;
        try (Session session = sessionFactory.openSession()) {
            try {
                Query<Location> query = session.createQuery("from Location where id = :id", Location.class);
                query.setParameter("id", id);
                location = Optional.of(query.getSingleResult());
                log.info("Location found");
            } catch (Exception e) {
                log.error("Error", e);
                return Optional.empty();
            }
            return location;
        }
    }
}
