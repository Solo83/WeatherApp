package com.solo83.weatherapp.repository;

import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.utils.config.HibernateUtil;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Optional;

@Slf4j
public class UserRepository {
    
    private static UserRepository INSTANCE;
    private final Session session = HibernateUtil.getSessionFactory().openSession();


        private UserRepository() {        
        }
        
        public static UserRepository getInstance() {
            if(INSTANCE == null) {
                INSTANCE = new UserRepository();
            }
            return INSTANCE;
        }


    public Optional<User> findByUserName(String userName) throws RepositoryException {
        Optional<User> findedUser;
        try (session) {
            try {
                Query<User> query = session.createQuery("from User where login = :userName", User.class);
                query.setParameter("userName", userName);
                findedUser = Optional.of(query.getSingleResult());
                log.info("User found");
            } catch (Exception e) {
                log.error("Error", e);
                throw new RepositoryException("Error while getting user");
            }
            return findedUser;
        }
    }

    public Optional<User> findByLocationId(String locationId) throws RepositoryException {
        Optional<User> findedUser;
        try (session) {
            try {
                Query<User> query = session.createQuery("from User u join Location l on u.id=l.user.id where l.id = :locationId", User.class);
                query.setParameter("locationId", locationId);
                findedUser = Optional.of(query.getSingleResult());
                log.info("User found");
            } catch (Exception e) {
                log.error("Error", e);
                throw new RepositoryException("Error while getting user");
            }
            return findedUser;
        }
    }

    public Optional<User> save(User user) throws RepositoryException {
        Optional<User> addedUser;
        Transaction transaction = null;
        try (session) {
            try {
                transaction = session.beginTransaction();
                session.persist(user);
                transaction.commit();
                addedUser = Optional.of(user);
                log.info("Added user: {}", addedUser.get());
            } catch (Exception e) {
                log.error("Error while adding user:", e);
                if (transaction != null) {
                    transaction.rollback();
                    log.info("Transaction is {}", transaction.getStatus());
                }
                throw new RepositoryException("Error while adding user");
            }
            return addedUser;
        }
    }

}
