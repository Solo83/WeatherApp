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


import java.util.Optional;

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
                 getServletContext().removeAttribute("LOGGED_USER");
             } catch (RepositoryException e) {
                 throw new RuntimeException(e);
             }
        }
        thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
    }
}
