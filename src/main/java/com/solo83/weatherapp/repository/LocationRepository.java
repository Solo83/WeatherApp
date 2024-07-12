package com.solo83.weatherapp.repository;

import com.solo83.weatherapp.entity.Location;

import com.solo83.weatherapp.utils.config.HibernateUtil;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

@Slf4j
public class LocationRepository {
    
    private static LocationRepository INSTANCE;
    
        private LocationRepository() {        
        }
        
        public static LocationRepository getInstance() {
            if(INSTANCE == null) {
                INSTANCE = new LocationRepository();
            }
            return INSTANCE;
        }


    public void save(Location entity) throws RepositoryException {
        Optional<Location> location;
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                transaction = session.beginTransaction();
                session.persist(entity);
                transaction.commit();
                location = Optional.of(entity);
                log.info("Location added: {}", location.get());
            } catch (Exception e) {
                log.error("Error while adding location");
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new RepositoryException("Error while adding location");
            }
        }
    }

    public void delete(Integer id) throws RepositoryException {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                transaction = session.beginTransaction();
                Location location = session.get(Location.class, id);
                session.remove(location);
                transaction.commit();
                log.info("Location deleted: {}", id);
            } catch (Exception e) {
                log.error("Error while deleting location {}", e.getMessage());
                if (transaction != null) {
                    transaction.rollback();
                    log.info("Transaction is {}", transaction.getStatus());
                }
                throw new RepositoryException("Error updating location");
            }
        }
    }

    public List<Location> findByUserID(Integer userId) throws RepositoryException {
        List<Location> findedLocations;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                Query<Location> query = session.createQuery("from Location as location where location.user.id = :id", Location.class);
                query.setParameter("id", userId);
                findedLocations = query.getResultList();
                log.info("Locations size {}",findedLocations);
            } catch (Exception e) {
                log.error("Error while getting Locations by userId");
                throw new RepositoryException("Error while getting Locations by userId");
            }
            return findedLocations;
        }
    }
}
