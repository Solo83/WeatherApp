package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.dto.GetLocationRequest;
import com.solo83.weatherapp.entity.Location;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.service.LocationService;
import com.solo83.weatherapp.service.UserService;
import com.solo83.weatherapp.utils.exception.ServiceException;
import com.solo83.weatherapp.utils.renderer.ThymeleafTemplateRenderer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@WebServlet("/main")
public class Main extends HttpServlet {

    private final ThymeleafTemplateRenderer thymeleafTemplateRenderer = ThymeleafTemplateRenderer.getInstance();
    UserService userService = UserService.getInstance();
    LocationService locationService = LocationService.getInstance();


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        List<GetLocationRequest> searchedLocations = (List<GetLocationRequest>) req.getAttribute("locations");
        User user = (User) req.getAttribute("user");;

        if (user == null){
        try {
            user = userService.getUserFromCookie(req);
        } catch (ServiceException ignored) {

        }}

        List<Location> userLocations = List.of();
        try {
            userLocations = locationService.getLocations(user.getId());
        } catch (ServiceException ignored) {
            req.setAttribute("error", "Error while retrieving user locations");
            thymeleafTemplateRenderer.renderTemplate(req, resp, "main");
        }

        List<GetLocationRequest> updatedLocation = List.of();
        
        try {
            updatedLocation  = locationService.getUpdatedLocation(userLocations);
        } catch (ServiceException e) {
            req.setAttribute("error", "Error while updating locations");
            thymeleafTemplateRenderer.renderTemplate(req, resp, "main");
        }

        req.setAttribute("user", user);
        req.setAttribute("locations", searchedLocations);
        req.setAttribute("userLocations", updatedLocation);

        thymeleafTemplateRenderer.renderTemplate(req, resp, "main");
    }

}
