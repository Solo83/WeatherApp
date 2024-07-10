package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.service.CookieService;
import com.solo83.weatherapp.service.SessionService;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import com.solo83.weatherapp.utils.renderer.ThymeleafTemplateRenderer;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


import java.util.Optional;

@Slf4j
@WebServlet("/logout")
public class Logout extends HttpServlet {

    private final CookieService cookieService = CookieService.getInstance();
    private final SessionService sessionService = SessionService.getInstance();
    private final ThymeleafTemplateRenderer thymeleafTemplateRenderer = ThymeleafTemplateRenderer.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

        Optional<Cookie> cookie = cookieService.getCookie(req);

         if (cookie.isPresent()) {
             try {
                 sessionService.remove(cookie.get().getValue());
                 cookieService.invalidateCookie(resp,cookie.get());
                 getServletContext().removeAttribute("locations");
             } catch (RepositoryException e) {
                 log.error(e.getMessage());
             }
        }

        req.setAttribute("success", "User logged out");
        log.info("User logged out");
        thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
    }
}
