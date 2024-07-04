package com.solo83.weatherapp.service;

import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.repository.SessionRepository;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Slf4j
public class SessionService {

    private final SessionRepository sessionRepository = SessionRepository.getInstance();
    private final SessionPersistanceService sessionPersistanceService = SessionPersistanceService.getInstance();
    private static final int SESSION_LIFETIME_IN_SECONDS = 5*60;

    private static SessionService INSTANCE;

    private SessionService() {}

    public static SessionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionService();
        }
        return INSTANCE;
    }


    public Optional<UserSession> getCurrentSession(User user) throws RepositoryException {
        String userId = user.getId().toString();
        Optional<UserSession> session = sessionRepository.findByUserId(userId);

        if (session.isPresent()) {
            UserSession currentSession = session.get();
            log.info("Session {} exists in DB, expires at {}", currentSession.getId(), currentSession.getExpiresAt());

            if (!isSessionValid(currentSession.getExpiresAt())) {
                log.info("Session is expired");
                setSessionExpirationTime(currentSession);
                sessionRepository.update(currentSession);
                log.info("Session updated, expires at {}", currentSession.getExpiresAt());
            }
            sessionPersistanceService.addSession(currentSession);
            return session;
        } else {
            return Optional.of(createAndSaveNewSession(user));
        }
    }

    public boolean isSessionValid(LocalDateTime expiresAt) {
        return LocalDateTime.now().isBefore(expiresAt);
    }

    public Optional<UserSession> getSessionById(String sessionId) throws RepositoryException {
        return sessionRepository.findById(sessionId);
    }

    private void setSessionExpirationTime(UserSession session) {
        session.setExpiresAt(LocalDateTime.now().plusSeconds(SESSION_LIFETIME_IN_SECONDS));
    }

    private UserSession createAndSaveNewSession(User user) throws RepositoryException {
        UserSession newSession = createNewSession(user);
        sessionPersistanceService.addSession(newSession);
        sessionRepository.save(newSession);
        log.info("New session created and saved to DB");
        return newSession;
    }

    private UserSession createNewSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(SESSION_LIFETIME_IN_SECONDS);
        return new UserSession(sessionId, user, expiresAt);
    }
}