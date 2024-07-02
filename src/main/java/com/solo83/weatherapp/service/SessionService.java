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
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class SessionService {

    Repository<String, UserSession> sessionRepository = SessionRepository.getInstance();

    private final Integer SESSION_LIFETIME_IN_SECONDS = 5;
    private static SessionService INSTANCE;

    private SessionService() {
    }

    public static SessionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionService();
        }
        return INSTANCE;
    }


    public HttpServletResponse setCookie(HttpServletRequest req, HttpServletResponse resp, User user) throws ServiceException, RepositoryException {

        Optional<UserSession> session;

        if (!isSessionAvailableInRequest(req)) {
            session = get(user.getId().toString());
            if (session.isPresent() && isSessionValid(session.get())) {
                setSessionExpirationTime(session.get());
                try {
                    sessionRepository.update(session.get());
                } catch (RepositoryException e) {
                    throw new ServiceException(e.getMessage());
                }
                log.info("Existind session getted from DB {}", session.get().getId());

            } else if (session.isEmpty()) {
                session = Optional.of(create(user));
                save(session.get());
                log.info("New session created and saved to DB");
            }

        } else {
            session = get(user.getId().toString());
            if(isSessionValid(session.get())) {
                setSessionExpirationTime(session.get());
                sessionRepository.update(session.get());
            }
        }

        Cookie cookie = new Cookie("userSession", session.get().getId());
        cookie.setAttribute("userId", user.getId().toString());
        cookie.setMaxAge(SESSION_LIFETIME_IN_SECONDS);
        resp.addCookie(cookie);

        log.info("Cookie set to session {}, expire at {}", cookie.getValue(), cookie.getMaxAge());

        return resp;

    }

    private boolean isSessionAvailableInRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        return (Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("userSession")).findFirst().isEmpty());
    }


    private Optional<UserSession> get(String userId) throws ServiceException {
        Optional<UserSession> session;
        try {
            session = sessionRepository.findById(userId);
        } catch (RepositoryException e) {
            session = Optional.empty();
        }
        return session;
    }

    private boolean isSessionValid(UserSession session) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expiresAt = session.getExpiresAt();

        return expiresAt.isBefore(currentTime);
    }

    private void setSessionExpirationTime(UserSession session) {
        session.setExpiresAt(LocalDateTime.now().plusSeconds(SESSION_LIFETIME_IN_SECONDS));
    }

    private void save(UserSession session) throws ServiceException {
        try {
            sessionRepository.save(session);
        } catch (RepositoryException e) {
            throw new ServiceException("Session already exist");
        }
    }

    private UserSession create(User user) {
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(SESSION_LIFETIME_IN_SECONDS);
        return new UserSession(sessionId, user, expiresAt);
    }

}
