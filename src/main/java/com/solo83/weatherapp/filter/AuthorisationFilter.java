package com.solo83.weatherapp.filter;

import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.service.CookieService;
import com.solo83.weatherapp.service.SessionService;
import com.solo83.weatherapp.utils.renderer.ThymeleafTemplateRenderer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
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

@Slf4j
@WebFilter("/*")
public class AuthorisationFilter implements Filter {

    private static final String ERROR_MESSAGE_NOT_FOUND = "Session not found, please SignIn";
    private static final String ERROR_MESSAGE_SESSION_EXPIRED = "Session is expired, please SignIn";
    private  SessionService sessionService;
    private  CookieService cookieService;
    private  ThymeleafTemplateRenderer thymeleafTemplateRenderer;

    @Override
    public void init(FilterConfig filterConfig) {
        thymeleafTemplateRenderer = ((ThymeleafTemplateRenderer) filterConfig.getServletContext().getAttribute("thymeleafTemplateRenderer"));
        sessionService = ((SessionService) filterConfig.getServletContext().getAttribute("sessionService"));
        cookieService = ((CookieService) filterConfig.getServletContext().getAttribute("cookieService"));
    }



    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        Optional<Cookie> cookie = cookieService.get(req);
        if (cookie.isPresent()) {
            log.info("Cookie found, authorised path");
            processSessionCookie(req, resp, chain, cookie.get());
        } else {
            log.info("Path without authorisation");
            chain.doFilter(req, resp);
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
                        invalidateSession(req, resp);
                    }
                },
                () -> handleInvalidatedSession(req, resp)
        );
    }

    private void handleValidSession(HttpServletRequest req, HttpServletResponse resp, FilterChain chain, UserSession userSession) {
        setUserAttribute(req, userSession.getUser());
        log.info("Session valid, USER is {}", userSession.getUser().getLogin());
        try {
            chain.doFilter(req, resp);
        } catch (IOException | ServletException e) {
            redirectHomeWithError(req, resp, e.getMessage());
        }
    }

    private void invalidateSession(HttpServletRequest req, HttpServletResponse resp) {
        sessionService.invalidate(req, resp);
        clearUserAttribute(req);
        redirectHomeWithError(req, resp, AuthorisationFilter.ERROR_MESSAGE_SESSION_EXPIRED);
    }

    private void handleInvalidatedSession(HttpServletRequest req, HttpServletResponse resp) {
        clearUserAttribute(req);
        cookieService.invalidate(req,resp);
        redirectHomeWithError(req, resp, ERROR_MESSAGE_NOT_FOUND);
    }

    private void setUserAttribute(HttpServletRequest req, User user ) {
        req.setAttribute("user", user);
    }

    private void clearUserAttribute(HttpServletRequest req) {
        req.removeAttribute("user");
    }

    private void redirectHomeWithError(HttpServletRequest req, HttpServletResponse resp, String errorMessage) {
        req.setAttribute("error", errorMessage);
        thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
    }
}