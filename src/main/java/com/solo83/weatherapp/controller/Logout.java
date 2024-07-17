package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.repository.SessionRepository;
import com.solo83.weatherapp.service.CookieService;
import com.solo83.weatherapp.service.SessionService;
import com.solo83.weatherapp.utils.config.HibernateUtil;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


@Slf4j
@WebServlet("/logout")
public class Logout extends HttpServlet {
    private final SessionService sessionService = SessionService.getInstance(SessionRepository.getInstance(HibernateUtil.getSessionFactory()), CookieService.getInstance());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        sessionService.invalidate(req, resp);
        log.info("User logged out");
        resp.sendRedirect("home");
    }
}