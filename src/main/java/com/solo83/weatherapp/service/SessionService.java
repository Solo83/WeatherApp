package com.solo83.weatherapp.service;

import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.repository.SessionRepository;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import com.solo83.weatherapp.utils.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
public class SessionService {

    private final SessionRepository sessionRepository = SessionRepository.getInstance();
    private static final int SESSION_LIFETIME_IN_SECONDS = 10*60;
    private final CookieService cookieService = CookieService.getInstance();
    private static SessionService INSTANCE;

    private SessionService() {}

    public static SessionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionService();
        }
        return INSTANCE;
    }

    public Optional<UserSession> getSession(User user, HttpServletResponse resp) throws RepositoryException, ServiceException {
        String userId = user.getId().toString();
        Optional<UserSession> session;

        try {
            session = sessionRepository.findByUserId(userId);
        } catch (RepositoryException e) {
            session = Optional.of(create(user));
            sessionRepository.save(session.get());
        }

        UserSession userSession = session.get();

        if (!isSessionValid(userSession.getExpiresAt())) {
            setSessionExpirationTime(userSession);
            sessionRepository.update(userSession);
        }

        cookieService.setCookie(resp,userSession.getId());

        return session;
    }

    public void remove(String sessionId) throws RepositoryException {
        sessionRepository.delete(sessionId);
    }

    public void invalidate(HttpServletRequest req, HttpServletResponse resp) throws RepositoryException {
        Optional<Cookie> cookie = cookieService.getCookie(req);
        String sessionId = cookie.get().getValue();
        cookieService.invalidateCookie(req,resp);
        remove(sessionId);
        log.info("Session invalidated {}", sessionId);
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

    private UserSession create(User user) {
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(SESSION_LIFETIME_IN_SECONDS);
        log.info("New session created: {}", sessionId);
        return new UserSession(sessionId, user, expiresAt);
    }


}