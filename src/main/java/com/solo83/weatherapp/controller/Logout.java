package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.service.SessionService;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


@Slf4j
@WebServlet("/logout")
public class Logout extends HttpServlet {

    private final SessionService sessionService = SessionService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        sessionService.invalidate(req, resp);
        log.info("User logged out");

        resp.sendRedirect("home");

    }
}