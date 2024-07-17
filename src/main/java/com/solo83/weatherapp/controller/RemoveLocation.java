package com.solo83.weatherapp.controller;

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
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;


@Slf4j
@WebServlet("/remove")
public class RemoveLocation extends HttpServlet {

    private final ThymeleafTemplateRenderer thymeleafTemplateRenderer = ThymeleafTemplateRenderer.getInstance();
    private final LocationService locationService = LocationService.getInstance(LocationRepository.getInstance(HibernateUtil.getSessionFactory()), OpenWeatherApiService.getInstance());
    private final UserService userService = UserService.getInstance(UserRepository.getInstance(HibernateUtil.getSessionFactory()), CookieService.getInstance(), SessionService.getInstance(SessionRepository.getInstance(HibernateUtil.getSessionFactory()),CookieService.getInstance()));

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer locationId = Integer.valueOf(req.getParameter("id"));
        Optional<User> user;
        try {
            user = userService.getUserByLocationId(locationId.toString());
        } catch (RepositoryException e) {
            req.setAttribute("error", e.getMessage());
            thymeleafTemplateRenderer.renderTemplate(req,resp,"home");
            return;
        }

        Optional<User> userFromRequest;

        try {
            userFromRequest = Optional.of(userService.getUserFromRequest(req));
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            thymeleafTemplateRenderer.renderTemplate(req,resp,"home");
            return;
        }

        if (user.isPresent() && user.get().equals(userFromRequest.get())) {
            locationService.removeLocation(locationId);
        }
        resp.sendRedirect("home");
    }
}
