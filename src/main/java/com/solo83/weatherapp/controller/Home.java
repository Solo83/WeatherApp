package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.dto.GetLocationRequest;
import com.solo83.weatherapp.entity.Location;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.repository.LocationRepository;
import com.solo83.weatherapp.repository.SessionRepository;
import com.solo83.weatherapp.repository.UserRepository;
import com.solo83.weatherapp.service.CookieService;
import com.solo83.weatherapp.service.LocationService;
import com.solo83.weatherapp.service.OpenWeatherApiService;
import com.solo83.weatherapp.service.SessionService;
import com.solo83.weatherapp.service.UserService;
import com.solo83.weatherapp.utils.config.HibernateUtil;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import com.solo83.weatherapp.utils.exception.ServiceException;
import com.solo83.weatherapp.utils.renderer.ThymeleafTemplateRenderer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@WebServlet("/home")
public class Home extends HttpServlet {
    private final ThymeleafTemplateRenderer thymeleafTemplateRenderer = ThymeleafTemplateRenderer.getInstance();
    private final UserService userService = UserService.getInstance(UserRepository.getInstance(HibernateUtil.getSessionFactory()), CookieService.getInstance(), SessionService.getInstance(SessionRepository.getInstance(HibernateUtil.getSessionFactory()),CookieService.getInstance()));
    private final LocationService locationService = LocationService.getInstance(LocationRepository.getInstance(HibernateUtil.getSessionFactory()), OpenWeatherApiService.getInstance());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

     User user;
        try {
            user = userService.getUserFromRequest(req);
        } catch (ServiceException e) {
            thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
            return;
        }

     if (user != null) {
         List<Location> userLocations = List.of();
         try {
             userLocations = locationService.getLocations(user.getId());
         } catch (RepositoryException e) {
             req.setAttribute("error", "Error while retrieving user locations");
             thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
         }

         List<GetLocationRequest> updatedLocation = List.of();

         try {
             updatedLocation = locationService.getUpdatedLocation(userLocations);
         } catch (ServiceException e) {
             req.setAttribute("error", "Error while updating locations");
             thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
         }

         req.setAttribute("user", user);
         req.setAttribute("userLocations", updatedLocation);
     }
        thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
    }

}
