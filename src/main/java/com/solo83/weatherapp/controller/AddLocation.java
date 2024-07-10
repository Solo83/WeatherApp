package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.dto.GetLocationRequest;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.service.LocationService;
import com.solo83.weatherapp.service.UserService;
import com.solo83.weatherapp.utils.exception.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@WebServlet("/addlocation")
public class AddLocation extends HttpServlet {
    LocationService locationService = LocationService.getInstance();
    UserService userService = UserService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        String name = req.getParameter("name");
        BigDecimal latitude = new BigDecimal(req.getParameter("latitude"));
        BigDecimal longitude = new BigDecimal(req.getParameter("longitude"));
        GetLocationRequest location = new GetLocationRequest(name,latitude,longitude);

        User user;
        try {
            user = userService.getUserFromCookie(req);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }

        locationService.addlocation(location,user);

        req.setAttribute("user", user);
        req.getRequestDispatcher("main").forward(req, resp);
    }
}
