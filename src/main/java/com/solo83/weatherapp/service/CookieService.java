package com.solo83.weatherapp.service;

import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import com.solo83.weatherapp.utils.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class CookieService {
    private static CookieService INSTANCE;

    private static final String SESSION_COOKIE = "userSession";
    private final SessionService sessionService = SessionService.getInstance();

        private CookieService() {
        }

        public static CookieService getInstance() {
            if(INSTANCE == null) {
                INSTANCE = new CookieService();
            }
            return INSTANCE;
        }

    public HttpServletResponse setCookie(HttpServletResponse resp, User user) throws ServiceException, RepositoryException {
        //String userId = user.getId().toString();
        UserSession currentSession = sessionService.get(user).orElseThrow(() -> new ServiceException("Failed to get or create session"));
        //int maxAgeInSeconds = calculateMaxAgeInSeconds(currentSession.getExpiresAt());
        int maxAgeInSeconds = -1;

        //Optional<Cookie> sessionCookieOpt = getSessionCookie(req);
        //Cookie sessionCookie = sessionCookieOpt.orElseGet(() -> new Cookie(SESSION_COOKIE, currentSession.getId()));
        Cookie sessionCookie = new Cookie(SESSION_COOKIE, currentSession.getId());

        sessionCookie.setMaxAge(maxAgeInSeconds);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true);
        resp.addCookie(sessionCookie);

        log.info("Cookie set to session {}, expires at {}", sessionCookie.getValue(), sessionCookie.getMaxAge());

       /* if (sessionCookieOpt.isPresent()) {
            log.info("Cookie MaxAge Updated {}, expires at {}", sessionCookie.getValue(), sessionCookie.getMaxAge());
        } else {
            log.info("New Cookie set to session {}, expires at {}", sessionCookie.getValue(), sessionCookie.getMaxAge());
        }*/

        return resp;
    }


    public Optional<Cookie> getSessionCookie(HttpServletRequest req) {
        return Optional.ofNullable(req.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> SESSION_COOKIE.equals(cookie.getName()))
                        .findFirst());
    }

    private int calculateMaxAgeInSeconds(LocalDateTime expiresAt) {
        long maxAgeInSeconds = Duration.between(LocalDateTime.now(), expiresAt).getSeconds();
        return maxAgeInSeconds < 0 ? 0 : (int) maxAgeInSeconds;
    }

}
