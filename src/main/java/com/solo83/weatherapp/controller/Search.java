package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.dto.GetLocationRequest;
import com.solo83.weatherapp.service.OpenWeatherApiService;
import com.solo83.weatherapp.utils.exception.ServiceException;
import com.solo83.weatherapp.utils.renderer.ThymeleafTemplateRenderer;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Search extends HttpServlet {

    protected final OpenWeatherApiService openWeatherApiService = OpenWeatherApiService.getInstance();
    protected final ThymeleafTemplateRenderer thymeleafTemplateRenderer = ThymeleafTemplateRenderer.getInstance();

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp, String redirectIfSuccess, String redirectIfError)   {
        String locationName = req.getParameter("locationName");

        if (locationName == null || locationName.isEmpty()) {
            req.setAttribute("error", "Enter a valid location name");
            thymeleafTemplateRenderer.renderTemplate(req, resp, redirectIfError);
            return;
        }

        List<GetLocationRequest> locations;

        try {
            locations = openWeatherApiService.getLocations(locationName);
        } catch (ServiceException e) {
            log.error(e.getMessage());
            req.setAttribute("error", "openWeatherApi error");
            thymeleafTemplateRenderer.renderTemplate(req, resp, redirectIfError);
            return;
        }

        if (locations.isEmpty()) {
            req.setAttribute("error", "Nothing found");
            thymeleafTemplateRenderer.renderTemplate(req, resp, redirectIfError);
            return;
        }
        req.setAttribute("locations", locations);
        req.setAttribute("locationName", locationName);

        thymeleafTemplateRenderer.renderTemplate(req, resp, redirectIfSuccess);
    }

}
