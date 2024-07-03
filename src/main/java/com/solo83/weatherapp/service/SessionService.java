package com.solo83.weatherapp.service;

import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.repository.Repository;
import com.solo83.weatherapp.repository.SessionRepository;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import com.solo83.weatherapp.utils.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SessionService {

    private final Repository<String, UserSession> sessionRepository = SessionRepository.getInstance();
    private static final int SESSION_LIFETIME_IN_SECONDS = 300;
    private static final String SESSION_COOKIE = "userSession";
    private static SessionService INSTANCE;

    private SessionService() {}

    public static SessionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionService();
        }
        return INSTANCE;
    }

    @Getter
    private static final Map<String, UserSession> sessions = new ConcurrentHashMap<>();

    public HttpServletResponse setCookie(HttpServletRequest req, HttpServletResponse resp, User user) throws ServiceException, RepositoryException {
        String userId = user.getId().toString();
        UserSession currentSession = getCurrentSession(user).orElseThrow(() -> new ServiceException("Failed to get or create session"));
        int maxAgeInSeconds = calculateMaxAgeInSeconds(currentSession.getExpiresAt());

        Optional<Cookie> sessionCookieOpt = getSessionCookie(req);
        Cookie sessionCookie = sessionCookieOpt.orElseGet(() -> new Cookie(SESSION_COOKIE, currentSession.getId()));

        sessionCookie.setAttribute("userId", userId);
        sessionCookie.setMaxAge(maxAgeInSeconds);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true);
        resp.addCookie(sessionCookie);

        if (sessionCookieOpt.isPresent()) {
            log.info("Cookie MaxAge Updated {}, expires at {}", sessionCookie.getValue(), sessionCookie.getMaxAge());
        } else {
            log.info("New Cookie set to session {}, expires at {}", sessionCookie.getValue(), sessionCookie.getMaxAge());
        }

        sessions.put(userId, currentSession);
        return resp;
    }

    public void removeSession(String userId) {
        sessions.remove(userId);
    }

    private int calculateMaxAgeInSeconds(LocalDateTime expiresAt) {
        long maxAgeInSeconds = Duration.between(LocalDateTime.now(), expiresAt).getSeconds();
        return maxAgeInSeconds < 0 ? 0 : (int) maxAgeInSeconds;
    }

    private Optional<Cookie> getSessionCookie(HttpServletRequest req) {
        return Optional.ofNullable(req.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> SESSION_COOKIE.equals(cookie.getName()))
                        .findFirst());
    }

    private Optional<UserSession> getCurrentSession(User user) throws RepositoryException {
        String userId = user.getId().toString();
        Optional<UserSession> session = sessionRepository.findById(userId);

        if (session.isPresent()) {
            UserSession currentSession = session.get();
            log.info("Session {} exists in DB", currentSession.getId());

            if (!isSessionValid(currentSession.getExpiresAt())) {
                log.info("Session is expired");
                setSessionExpirationTime(currentSession);
                sessionRepository.update(currentSession);
                log.info("Session updated, expires at {}", currentSession.getExpiresAt());
            }
            return session;
        } else {
            return Optional.of(createAndSaveNewSession(user));
        }
    }

    private boolean isSessionValid(LocalDateTime expiresAt) {
        return LocalDateTime.now().isBefore(expiresAt);
    }

    private void setSessionExpirationTime(UserSession session) {
        session.setExpiresAt(LocalDateTime.now().plusSeconds(SESSION_LIFETIME_IN_SECONDS));
    }

    private UserSession createAndSaveNewSession(User user) throws RepositoryException {
        UserSession newSession = createNewSession(user);
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