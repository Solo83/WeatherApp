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
import java.util.Optional;
import java.util.Set;

@Slf4j
@WebFilter("/*")
public class LoginFilter implements Filter {

    private static final Set<String> ALLOWED_PATHS = Set.of("/signin", "/signup", "/home", "");
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

        Optional<Cookie> cookie = cookieService.getCookie(req);

        if (cookie.isPresent()) {
            processSessionCookie(req, resp, chain, cookie.get());
        } else {
            redirectHomeWithError(req, resp, ERROR_MESSAGE_SIGN_IN);
        }
    }

    private void processSessionCookie(HttpServletRequest req, HttpServletResponse resp, FilterChain chain, Cookie cookie) {
        String sessionId = cookie.getValue();
        Optional<UserSession> session = sessionService.getById(sessionId);

        session.ifPresentOrElse(
                userSession -> {
                    if (sessionService.isSessionValid(userSession.getExpiresAt())) {
                        handleValidSession(req, resp, chain, userSession);
                    } else {
                        invalidateSession(req, resp, cookie, sessionId);
                    }
                },
                () -> handleInvalidSession(req, resp, cookie)
        );
    }

    private void handleValidSession(HttpServletRequest req, HttpServletResponse resp, FilterChain chain, UserSession userSession) {
        setUserAttribute(req, userSession.getUser());
        log.info("Session valid, USER is {}", userSession.getUser().getLogin());
        try {
            chain.doFilter(req, resp);
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }

    private void invalidateSession(HttpServletRequest req, HttpServletResponse resp, Cookie cookie, String sessionId) {
        try {
            sessionService.remove(sessionId);
            cookieService.invalidateCookie(resp, cookie);
            clearUserAttribute(req);
            redirectHomeWithError(req, resp, LoginFilter.ERROR_MESSAGE_SESSION_EXPIRED);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleInvalidSession(HttpServletRequest req, HttpServletResponse resp, Cookie cookie) {
        clearUserAttribute(req);
        cookieService.invalidateCookie(resp, cookie);
        redirectHomeWithError(req, resp, ERROR_MESSAGE_SIGN_IN);
    }

    private void setUserAttribute(HttpServletRequest req, User user ) {
        req.getServletContext().setAttribute("user", user);
    }

    private void clearUserAttribute(HttpServletRequest req) {
        req.getServletContext().removeAttribute("user");
    }

    private String extractPath(HttpServletRequest req) {
        return req.getRequestURI().substring(req.getContextPath().length()).replaceAll("[/]+$", "");
    }

    private void redirectHomeWithError(HttpServletRequest req, HttpServletResponse resp, String errorMessage) {
        req.setAttribute("error", errorMessage);
        thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
    }
}