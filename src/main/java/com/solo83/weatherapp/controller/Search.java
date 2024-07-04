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

import java.util.List;

@Slf4j
@WebServlet("/search")
public class Search extends HttpServlet {

    ThymeleafTemplateRenderer thymeleafTemplateRenderer = ThymeleafTemplateRenderer.getInstance();
    OpenWeatherApiService openWeatherApiService = OpenWeatherApiService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String locationName = req.getParameter("locationName");

        if (locationName == null || locationName.isEmpty()) {
            req.setAttribute("error", "Enter a valid location name");
            thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
            return;
        }

        List<GetLocationRequest> locations;

        try {
            locations = openWeatherApiService.getLocations(locationName);
        } catch (ServiceException e) {
            log.error(e.getMessage());
            req.setAttribute("error", "openWeatherApi error");
            thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
            return;
        }

        req.setAttribute("locations", locations);
        req.setAttribute("locationName", locationName);

        if (locations.isEmpty()) {
            req.setAttribute("error", "Nothing finded");
            thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
            return;
        }

        thymeleafTemplateRenderer.renderTemplate(req, resp, "search");
    }
}

