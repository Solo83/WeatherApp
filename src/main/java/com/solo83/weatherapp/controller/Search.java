package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.dto.GetLocationRequest;
import com.solo83.weatherapp.service.OpenWeatherApiService;
import com.solo83.weatherapp.utils.exception.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
@WebServlet("/search")
public class Search extends HttpServlet {

    protected final OpenWeatherApiService openWeatherApiService = OpenWeatherApiService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String locationName = req.getParameter("locationName");

        if (locationName == null || locationName.isEmpty()) {
            req.setAttribute("error", "Enter a valid location name");
            req.getRequestDispatcher("home").forward(req, resp);
            return;
        }

        List<GetLocationRequest> locations;

        try {
            locations = openWeatherApiService.getLocations(locationName);
        } catch (ServiceException e) {
            log.error(e.getMessage());
            req.setAttribute("error", "openWeatherApi error");
            req.getRequestDispatcher("home").forward(req, resp);
            return;
        }

        if (locations.isEmpty()) {
            req.setAttribute("error", "Nothing found");
            req.getRequestDispatcher("home").forward(req, resp);
            return;
        }

        req.setAttribute("locations", locations);
        req.setAttribute("locationName", locationName);

        req.getRequestDispatcher("home").forward(req, resp);
    }
}

