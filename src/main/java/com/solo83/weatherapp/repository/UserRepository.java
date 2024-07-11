package com.solo83.weatherapp.repository;

import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.utils.config.HibernateUtil;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

@Slf4j
public class UserRepository implements Repository<Integer,User> {
    
    private static UserRepository INSTANCE;
    
        private UserRepository() {        
        }
        
        public static UserRepository getInstance() {
            if(INSTANCE == null) {
                INSTANCE = new UserRepository();
            }
            return INSTANCE;
        }


    @Override
    public Optional<User> findById(Integer id) throws RepositoryException {
        Optional<User> findedUser;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                Query<User> query = session.createQuery("from User where id = :id", User.class);
                query.setParameter("id", id);
                findedUser = Optional.of(query.getSingleResult());
                log.info("User found");
            } catch (Exception e) {
                log.error("Error", e);
                throw new RepositoryException("Error while getting user");
            }
            return findedUser;
        }
    }

    public Optional<User> findByUserName(String userName) throws RepositoryException {
        Optional<User> findedUser;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
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

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public Optional<User> save(User user) throws RepositoryException {
        Optional<User> addedUser;
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
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

    @Override
    public boolean delete(Integer id) throws RepositoryException {
        return false;
    }

    @Override
    public Optional<User> update(User entity) throws RepositoryException {
        return Optional.empty();
    }
}
