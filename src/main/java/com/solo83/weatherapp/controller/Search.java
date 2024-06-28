package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.dto.GetLocationRequest;
import com.solo83.weatherapp.service.OpenWeatherApiService;
import com.solo83.weatherapp.utils.renderer.ThymeleafTemplateRenderer;
import com.solo83.weatherapp.utils.exception.ServiceException;
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

    ThymeleafTemplateRenderer thymeleafTemplateRenderer = ThymeleafTemplateRenderer.getInstance();
    OpenWeatherApiService openWeatherApiService = OpenWeatherApiService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String locationName = req.getParameter("locationName");

        if (locationName == null || locationName.isEmpty()) {
            resp.sendRedirect("home");
        }

        List<GetLocationRequest> locations = List.of();

        try {
            locations = openWeatherApiService.getLocations(locationName);
        } catch (ServiceException e) {
            log.error(e.getMessage());
            req.setAttribute("error", "Error occurred while getting locations");
        }

        req.setAttribute("locations", locations);
        req.setAttribute("locationName", locationName);

        if (locations.isEmpty()) {
            resp.sendRedirect("home");
        }

        thymeleafTemplateRenderer.renderTemplate(req, resp, "search");
    }
}

