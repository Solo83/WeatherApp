package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.dto.LocationFromRequest;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.service.LocationService;
import com.solo83.weatherapp.service.UserService;
import com.solo83.weatherapp.utils.renderer.ThymeleafTemplateRenderer;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@WebServlet("/add")
public class LocationAddServlet extends HttpServlet {
    private ThymeleafTemplateRenderer thymeleafTemplateRenderer;
    private UserService userService;
    private LocationService locationService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userService = ((UserService) getServletContext().getAttribute("userService"));
        locationService = ((LocationService) getServletContext().getAttribute("locationService"));
        thymeleafTemplateRenderer = ((ThymeleafTemplateRenderer) getServletContext().getAttribute("thymeleafTemplateRenderer"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        BigDecimal latitude = new BigDecimal(req.getParameter("latitude"));
        BigDecimal longitude = new BigDecimal(req.getParameter("longitude"));
        LocationFromRequest location = new LocationFromRequest(name, latitude, longitude);
        User user = null;
        try {
            user = userService.getUserFromRequest(req);
            locationService.addLocation(location, user);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            List<LocationFromRequest> userLocations = locationService.getUpdatedLocation(locationService.getLocations(user.getId()));
            req.setAttribute("userLocations", userLocations);
            thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
            return;
        }

        resp.sendRedirect("home");
    }
}
