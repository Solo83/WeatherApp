package com.solo83.weatherapp.service;

import com.solo83.weatherapp.utils.exception.RepositoryException;
import com.solo83.weatherapp.utils.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class CookieService {
    private static CookieService INSTANCE;

    private static final String SESSION_COOKIE = "userSession";

        private CookieService() {
        }

        public static CookieService getInstance() {
            if(INSTANCE == null) {
                INSTANCE = new CookieService();
            }
            return INSTANCE;
        }

    public void setCookie(HttpServletResponse resp, String sessionId) throws ServiceException, RepositoryException {

        int maxAgeInSeconds = -1;
        Cookie sessionCookie = new Cookie(SESSION_COOKIE, sessionId);
        sessionCookie.setMaxAge(maxAgeInSeconds);
        //sessionCookie.setHttpOnly(true);
        //sessionCookie.setSecure(true);
        resp.addCookie(sessionCookie);

        log.info("Cookie set to session {}, expires at {}", sessionCookie.getValue(), sessionCookie.getMaxAge());

    }

    public Optional<Cookie> getCookie(HttpServletRequest req) {
        return Optional.ofNullable(req.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> SESSION_COOKIE.equals(cookie.getName()))
                        .findFirst());
    }

    public void invalidateCookie(HttpServletResponse resp, Cookie cookie) {
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
        log.info("Cookie invalidated");
    }

}
