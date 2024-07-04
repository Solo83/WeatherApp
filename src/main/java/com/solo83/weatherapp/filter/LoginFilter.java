package com.solo83.weatherapp.filter;

import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.service.CookieService;
import com.solo83.weatherapp.service.SessionPersistanceService;
import com.solo83.weatherapp.service.SessionService;
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
            Arrays.asList("/signin", "/signup", "/home","")));

    private static final SessionService sessionService = SessionService.getInstance();
    private static final SessionPersistanceService sessionPersistanceService = SessionPersistanceService.getInstance();
    private final CookieService cookieService = CookieService.getInstance();
    private final ThymeleafTemplateRenderer thymeleafTemplateRenderer = ThymeleafTemplateRenderer.getInstance();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        Optional<Cookie> cookie = cookieService.getSessionCookie(req);
        String path = req.getRequestURI().substring(req.getContextPath().length()).replaceAll("[/]+$", "");
        boolean allowedPath = ALLOWED_PATHS.contains(path);

        if(allowedPath) {
            chain.doFilter(request, response);
            return;
        }

            if (cookie.isPresent()) {
                String sessionId = cookie.get().getValue();
                UserSession session;
                try {
                    session = sessionPersistanceService.getSession(sessionId);
                    if (!sessionService.isSessionValid(session.getExpiresAt())) {
                        sessionPersistanceService.removeSession(sessionId);
                        req.setAttribute("error", "Session expired, please SignIn");
                        thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
                        return;
                    }
                } catch (NullPointerException e) {
                    req.setAttribute("error", "Please SignIn");
                    thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
                    return;
                }
                chain.doFilter(request, response);
            }

}   }
