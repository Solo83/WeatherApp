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

    Repository<String, UserSession> sessionRepository = SessionRepository.getInstance();

    private final Integer SESSION_LIFETIME_IN_SECONDS = 300;
    private final String SESSION_COOKIE = "userSession";
    private static SessionService INSTANCE;

    private SessionService() {
    }

    public static SessionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionService();
        }
        return INSTANCE;
    }

    @Getter
    private static Map<String, UserSession> sessions = new ConcurrentHashMap<>();

    public HttpServletResponse setCookie(HttpServletRequest req, HttpServletResponse resp, User user) throws ServiceException, RepositoryException {

        String userId = user.getId().toString();
        UserSession currentSession = getCurrentSession(user).get();

        int maxAgeInSeconds = calculateMaxAgeInSeconds(currentSession.getExpiresAt());

        if (!isSessionCookieAvailableInRequest(req)) {
            Cookie cookie = new Cookie(SESSION_COOKIE, currentSession.getId());
            cookie.setAttribute("userId", userId);
            cookie.setMaxAge(maxAgeInSeconds);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            resp.addCookie(cookie);
            log.info("New Cookie set to session {}, expires at {}", cookie.getValue(), cookie.getMaxAge());
        } else {
            Cookie cookie = getSessionCookie(req).get();
            cookie.setMaxAge(maxAgeInSeconds);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            resp.addCookie(cookie);
            log.info("Cookie MaxAge Updated {}, expires at {}", cookie.getValue(), cookie.getMaxAge());
        }

        sessions.put(userId, currentSession);
        return resp;
    }

    public void removeSession(String userId) {
        sessions.remove(userId);
    }


    private boolean isSessionCookieAvailableInRequest(HttpServletRequest req) {
        Optional<Cookie> sessionCookie = getSessionCookie(req);
        return sessionCookie.isPresent();
    }

    private int calculateMaxAgeInSeconds(LocalDateTime expiresAt) {
        LocalDateTime now = LocalDateTime.now();
        long maxAgeInSeconds = Duration.between(now, expiresAt).getSeconds();
        return maxAgeInSeconds < 0 ? 0 : (int) maxAgeInSeconds;
    }

    private Optional<Cookie> getSessionCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(SESSION_COOKIE)).findFirst();
    }

    private Optional<UserSession> getCurrentSession(User user) throws RepositoryException {
        Optional<UserSession> session;
        try {
            session = sessionRepository.findById(user.getId().toString());
        } catch (RepositoryException e) {
            session = Optional.empty();
        }

        if (session.isPresent()) {
            UserSession currentSession = session.get();
            log.info("Session {} exists in DB", currentSession.getId());

            if (!isSessionValid(currentSession.getExpiresAt())) {
                log.info("Session is expired");
                setSessionExpirationTime(currentSession);
                sessionRepository.update(currentSession);
                log.info("Session updated, expires at {}", currentSession.getExpiresAt());
            }
        } else {
            session = Optional.of(createNewSession(user));
            save(session.get());
            log.info("New session created and saved to DB");
        }
        return session;
    }

    private boolean isSessionValid(LocalDateTime expiresAt) {
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.isBefore(expiresAt);
    }

    private void setSessionExpirationTime(UserSession session) {
        session.setExpiresAt(LocalDateTime.now().plusSeconds(SESSION_LIFETIME_IN_SECONDS));
    }

    private void save(UserSession session) throws RepositoryException {
        sessionRepository.save(session);
    }

    private UserSession createNewSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(SESSION_LIFETIME_IN_SECONDS);
        return new UserSession(sessionId, user, expiresAt);
    }

}
