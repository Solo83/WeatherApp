package com.solo83.weatherapp.service;

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
        if (INSTANCE == null) {
            INSTANCE = new CookieService();
        }
        return INSTANCE;
    }

    public void set(HttpServletResponse resp, String sessionId) {

        int maxAgeInSeconds = -1;
        Cookie sessionCookie = new Cookie(SESSION_COOKIE, sessionId);
        sessionCookie.setMaxAge(maxAgeInSeconds);
        resp.addCookie(sessionCookie);

        log.info("Cookie set to session {}, expires at {}", sessionCookie.getValue(), sessionCookie.getMaxAge());
    }

    public Optional<Cookie> get(HttpServletRequest req) {
        return Optional.ofNullable(req.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> SESSION_COOKIE.equals(cookie.getName()))
                        .findFirst());
    }

    public void invalidate(HttpServletRequest req, HttpServletResponse resp) {
        Cookie cookie = get(req).get();
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
        log.info("Cookie invalidated {}", cookie.getValue());
    }

}
