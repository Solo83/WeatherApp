package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.dto.GetLocationRequest;
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

@Slf4j
@WebServlet("/add")
public class AddLocation extends HttpServlet {
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
        GetLocationRequest location = new GetLocationRequest(name, latitude, longitude);
        User user = null;
        try {
            user = userService.getUserFromRequest(req);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
        }
        locationService.addLocation(location, user);
        resp.sendRedirect("home");
    }
}
