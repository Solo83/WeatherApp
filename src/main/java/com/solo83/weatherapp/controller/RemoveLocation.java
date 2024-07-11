package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.service.LocationService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


@Slf4j
@WebServlet("/remove")
public class RemoveLocation extends HttpServlet {

    LocationService locationService = LocationService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Integer id = Integer.valueOf(req.getParameter("id"));
        locationService.removeLocation(id);
        resp.sendRedirect("home");

    }
}
