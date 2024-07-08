package com.solo83.weatherapp.repository;

import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.utils.config.HibernateUtil;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

@Slf4j
public class SessionRepository implements Repository<String, UserSession> {

    private static SessionRepository INSTANCE;

        private SessionRepository() {
        }

        public static SessionRepository getInstance() {
            if(INSTANCE == null) {
                INSTANCE = new SessionRepository();
            }
            return INSTANCE;
        }


    public Optional<UserSession> findByUserId(String userId) throws RepositoryException {
        Optional<UserSession> findedSession;
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                transaction = session.beginTransaction();
                Query<UserSession> query = session.createQuery("from UserSession as session where session.user.id = :id", UserSession.class);
                query.setParameter("id", userId);
                findedSession = Optional.of(query.getSingleResult());
                log.info("Finded session: {}", findedSession.get().getId());
            } catch (Exception e) {
                log.error("Error while getting session by userId:", e);
                if (transaction != null) {
                    transaction.rollback();
                    log.info("Transaction is {}", transaction.getStatus());
                }
                throw new RepositoryException("Error while getting session by userId");
            }
            return findedSession;
        }
    }

    @Override
    public Optional<UserSession> findById(String id) throws RepositoryException {
        Optional<UserSession> findedSession;
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                transaction = session.beginTransaction();
                Query<UserSession> query = session.createQuery("from UserSession where id = :id", UserSession.class);
                query.setParameter("id", id);
                findedSession = Optional.of(query.getSingleResult());
                log.info("Finded session: {}", findedSession.get().getId());
            } catch (Exception e) {
                log.error("Error while getting session by Id:", e);
                if (transaction != null) {
                    transaction.rollback();
                    log.info("Transaction is {}", transaction.getStatus());
                }
                throw new RepositoryException("Error while getting session by Id");
            }
            return findedSession;
        }
    }

    @Override
    public List<UserSession> findAll() throws RepositoryException {
        Transaction transaction = null;
        List<UserSession> userSessions;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<UserSession> query = session.createQuery("from UserSession ", UserSession.class);
            userSessions = query.getResultList();
            log.info("Finded userSessions {}", userSessions.size());
            transaction.commit();
        } catch (Exception e) {
            log.error("Error while getting userSessions:", e);
            if (transaction != null) {
                transaction.rollback();
                log.info("Transaction is {}", transaction.getStatus());
            }
            throw new RepositoryException("Error while getting userSessions");
        }
        return userSessions;
    }

    @Override
    public Optional<UserSession> save(UserSession userSession) throws RepositoryException {
        Optional<UserSession> addedSession;
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                transaction = session.beginTransaction();
                session.persist(userSession);
                transaction.commit();
                addedSession = Optional.of(userSession);
                log.info("Added session: {}", addedSession.get());
            } catch (Exception e) {
                log.error("Error while adding session:", e);
                if (transaction != null) {
                    transaction.rollback();
                    log.info("Transaction is {}", transaction.getStatus());
                }
                throw new RepositoryException("Error while adding session");
            }
            return addedSession;
        }
    }

    @Override
    public boolean delete(String id) throws RepositoryException {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                transaction = session.beginTransaction();
                UserSession userSession = session.get(UserSession.class, id);
                session.remove(userSession);
                transaction.commit();
                log.info("Session deleted: {}", userSession.getId());
            } catch (Exception e) {
                log.error("Error while deleting session:", e);
                if (transaction != null) {
                    transaction.rollback();
                    log.info("Transaction is {}", transaction.getStatus());
                }
                throw new RepositoryException("Error updating session");
            }
            return true;
        }
    }

    @Override
    public Optional<UserSession> update(UserSession userSession) throws RepositoryException {
        Optional<UserSession> updatedSession;
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                transaction = session.beginTransaction();
                session.merge(userSession);
                transaction.commit();
                updatedSession = Optional.of(userSession);
                log.info("Session updated: {}", updatedSession.get().getId());
            } catch (Exception e) {
                log.error("Error while updating session:", e);
                if (transaction != null) {
                    transaction.rollback();
                    log.info("Transaction is {}", transaction.getStatus());
                }
                throw new RepositoryException("Error updating session");
            }
            return updatedSession;
        }
    }
}
