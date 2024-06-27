package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.dto.GetUserRequest;
import com.solo83.weatherapp.service.UserService;
import com.solo83.weatherapp.utils.exception.ServiceException;
import com.solo83.weatherapp.utils.exception.ValidatorException;
import com.solo83.weatherapp.utils.renderer.ThymeleafTemplateRenderer;
import com.solo83.weatherapp.utils.validator.InputValidator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class Register extends HttpServlet {

    private final ThymeleafTemplateRenderer thymeleafTemplateRenderer = ThymeleafTemplateRenderer.getInstance();
    private final InputValidator validator = new InputValidator();
    private final UserService userService = UserService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        thymeleafTemplateRenderer.renderTemplate(req,resp,"register");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        try {
            validator.validateUserName(req.getParameterMap(),"username");
            validator.validatePassword(req.getParameterMap(),"password","password_confirm");

            String username = req.getParameter("username");
            String password = req.getParameter("password");

            userService.save(new GetUserRequest(username,password));

        } catch (ValidatorException | ServiceException e) {
            req.setAttribute("error", e.getMessage());
            thymeleafTemplateRenderer.renderTemplate(req, resp, "register");
            return;
        }

        thymeleafTemplateRenderer.renderTemplate(req, resp, "home");

    }
}
