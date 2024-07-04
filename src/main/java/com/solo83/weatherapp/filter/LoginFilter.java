package com.solo83.weatherapp.filter;

import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.service.CookieService;
import com.solo83.weatherapp.service.SessionService;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import com.solo83.weatherapp.utils.renderer.ThymeleafTemplateRenderer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@WebFilter("/*")
public class LoginFilter implements Filter {

    private static final Set<String> ALLOWED_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("/signin", "/signup", "/home", "")));

    private static final String ERROR_MESSAGE_SIGN_IN = "Please SignIn";
    private static final String ERROR_MESSAGE_SESSION_EXPIRED = "Session expired, please SignIn";

    private static final SessionService sessionService = SessionService.getInstance();
    private final CookieService cookieService = CookieService.getInstance();
    private final ThymeleafTemplateRenderer thymeleafTemplateRenderer = ThymeleafTemplateRenderer.getInstance();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        String path = extractPath(req);
        boolean allowedPath = ALLOWED_PATHS.contains(path);

        if (allowedPath) {
            chain.doFilter(request, response);
            return;
        }

        Optional<Cookie> cookie = cookieService.getSessionCookie(req);
        if (cookie.isPresent()) {
            processSessionCookie(req, resp, chain, cookie.get());
        } else {
            redirectToHomeWithError(req, resp, ERROR_MESSAGE_SIGN_IN);
        }
    }

    private String extractPath(HttpServletRequest req) {
        return req.getRequestURI().substring(req.getContextPath().length()).replaceAll("[/]+$", "");
    }

    private void processSessionCookie(HttpServletRequest req, HttpServletResponse resp, FilterChain chain, Cookie cookie) throws IOException, ServletException {
        String sessionId = cookie.getValue();
        Optional<UserSession> session;

        try {
            session = sessionService.getById(sessionId);
        } catch (RepositoryException e) {
            session = Optional.empty();
        }

        if (session.isEmpty()) {
            handleInvalidSession(req, resp, cookie, ERROR_MESSAGE_SIGN_IN);
        } else {
            boolean sessionValid = sessionService.isSessionValid(session.get().getExpiresAt());
            if (!sessionValid) {
                handleExpiredSession(req, resp, cookie, sessionId);
            } else {
                User user = session.get().getUser();
                req.getServletContext().setAttribute("LOGGED_USER", user.getLogin());
                log.info("LOGGED_USER is {}", user.getLogin());
                chain.doFilter(req, resp);
            }
        }
    }

    private void handleInvalidSession(HttpServletRequest req, HttpServletResponse resp, Cookie cookie, String errorMessage) {
        req.getServletContext().setAttribute("LOGGED_USER", "");
        invalidateCookie(resp, cookie);
        redirectToHomeWithError(req, resp, errorMessage);
    }

    private void handleExpiredSession(HttpServletRequest req, HttpServletResponse resp, Cookie cookie, String sessionId) {
        try {
            sessionService.remove(sessionId);
            invalidateCookie(resp, cookie);
        } catch (RepositoryException e) {
            log.error("Error removing session", e);
        }
        handleInvalidSession(req, resp, cookie, ERROR_MESSAGE_SESSION_EXPIRED);
    }

    private void invalidateCookie(HttpServletResponse resp, Cookie cookie) {
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }

    private void redirectToHomeWithError(HttpServletRequest req, HttpServletResponse resp, String errorMessage) {
        req.setAttribute("error", errorMessage);
        thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
    }
}