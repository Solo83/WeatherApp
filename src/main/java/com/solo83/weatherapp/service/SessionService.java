package com.solo83.weatherapp.service;

import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.repository.SessionRepository;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import com.solo83.weatherapp.utils.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
public class SessionService {

    private final SessionRepository sessionRepository = SessionRepository.getInstance();
    private static final int SESSION_LIFETIME_IN_SECONDS = 5*60;

    private static SessionService INSTANCE;

    private SessionService() {}

    public static SessionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionService();
        }
        return INSTANCE;
    }


    public UserSession get (User user) throws RepositoryException, ServiceException {
        return getUserSession(user).orElseThrow(() -> new ServiceException("Failed to get or create session"));
    }

    private Optional<UserSession> getUserSession(User user) throws RepositoryException {
        String userId = user.getId().toString();
        Optional<UserSession> session;


        try {
            session = sessionRepository.findByUserId(userId);
        } catch (RepositoryException e) {
            return Optional.of(createAndSave(user));
        }

        if (session.isPresent()) {
            UserSession currentSession = session.get();
            log.info("Session {} exists in DB, expires at {}", currentSession.getId(), currentSession.getExpiresAt());

            if (!isSessionValid(currentSession.getExpiresAt())) {
                log.info("Session is expired");
                setSessionExpirationTime(currentSession);
                sessionRepository.update(currentSession);
                log.info("Session updated, expires at {}", currentSession.getExpiresAt());
            }

            return session;

        } else {
            return Optional.of(createAndSave(user));
        }
    }

    public void remove(String sessionId) throws RepositoryException {
        sessionRepository.delete(sessionId);
    }

    public List<UserSession> getAll() throws RepositoryException {
        return sessionRepository.findAll();
    }

    public boolean isSessionValid(LocalDateTime expiresAt) {
        return LocalDateTime.now().isBefore(expiresAt);
    }

    public Optional<UserSession> getById(String sessionId) {
        try {
            return sessionRepository.findById(sessionId);
        } catch (RepositoryException e) {
            return Optional.empty();
        }
    }

    private void setSessionExpirationTime(UserSession session) {
        session.setExpiresAt(LocalDateTime.now().plusSeconds(SESSION_LIFETIME_IN_SECONDS));
    }

    private UserSession createAndSave(User user) throws RepositoryException {
        UserSession newSession = create(user);
        sessionRepository.save(newSession);
        log.info("New session created and saved to DB");
        return newSession;
    }

    private UserSession create(User user) {
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(SESSION_LIFETIME_IN_SECONDS);
        return new UserSession(sessionId, user, expiresAt);
    }


}