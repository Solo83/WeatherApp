package com.solo83.weatherapp.service;

import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.repository.SessionRepository;
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

    private final SessionRepository sessionRepository ;
    private static final int SESSION_LIFETIME_IN_SECONDS = 10*60;
    private final CookieService cookieService;
    private static SessionService INSTANCE;

    private SessionService(SessionRepository sessionRepository, CookieService cookieService) {
        this.sessionRepository = sessionRepository;
        this.cookieService = cookieService;
    }

    public static SessionService getInstance(SessionRepository sessionRepository,CookieService cookieService) {
        if (INSTANCE == null) {
            INSTANCE = new SessionService(sessionRepository,cookieService);
        }
        return INSTANCE;
    }

    public Optional<UserSession> get(User user, HttpServletResponse resp) {
        String userId = user.getId().toString();
        Optional<UserSession> session;
        try {
            session = sessionRepository.findByUserId(userId);
        } catch (Exception e) {
            session = Optional.of(create(user));
            sessionRepository.save(session.get());
        }
        UserSession userSession = session.get();
        if (!isSessionValid(userSession.getExpiresAt())) {
            setSessionExpirationTime(userSession);
            sessionRepository.update(userSession);
        }
        cookieService.set(resp,userSession.getId());
        return session;
    }

    public void remove(String sessionId) {
        sessionRepository.delete(sessionId);
    }

    public void invalidate(HttpServletRequest req, HttpServletResponse resp)  {
        Optional<Cookie> cookie = cookieService.get(req);
        String sessionId = cookie.get().getValue();
        try {
            remove(sessionId);
        } catch (Exception e) {
            log.error(e.getMessage());
            cookieService.invalidate(req,resp);
        }
        cookieService.invalidate(req,resp);
        log.info("Session invalidated {}", sessionId);
    }

    public List<UserSession> getAll() {
        return sessionRepository.findAll();
    }

    public boolean isSessionValid(LocalDateTime expiresAt) {
        return LocalDateTime.now().isBefore(expiresAt);
    }

    public Optional<UserSession> getById(String sessionId) {
        try {
            return sessionRepository.findById(sessionId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    void setSessionExpirationTime(UserSession session) {
        session.setExpiresAt(LocalDateTime.now().plusSeconds(SESSION_LIFETIME_IN_SECONDS));
    }

    UserSession create(User user) {
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(SESSION_LIFETIME_IN_SECONDS);
        log.info("New session created: {}", sessionId);
        return new UserSession(sessionId, user, expiresAt);
    }


}