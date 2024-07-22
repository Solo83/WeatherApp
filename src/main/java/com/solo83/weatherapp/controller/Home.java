package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.dto.GetLocationRequest;
import com.solo83.weatherapp.entity.Location;
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

import java.util.List;

@WebServlet("/home")
public class Home extends HttpServlet {
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        User user;
        try {
            user = userService.getUserFromRequest(req);
        } catch (Exception e) {
            thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
            return;
        }
        if (user != null) {
            List<Location> userLocations = List.of();
            try {
                userLocations = locationService.getLocations(user.getId());
            } catch (Exception e) {
                req.setAttribute("error", "Error while retrieving user locations");
                thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
            }
            List<GetLocationRequest> updatedLocation = List.of();
            try {
                updatedLocation = locationService.getUpdatedLocation(userLocations);
            } catch (Exception e) {
                req.setAttribute("error", "Error while updating locations");
                thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
            }
            req.setAttribute("user", user);
            req.setAttribute("userLocations", updatedLocation);
        }
        thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
    }

}
