package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.dto.UserFromRequest;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.service.SessionService;
import com.solo83.weatherapp.service.UserService;
import com.solo83.weatherapp.utils.renderer.ThymeleafTemplateRenderer;
import com.solo83.weatherapp.utils.validator.InputValidator;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/signup")
public class UserSignUpServlet extends HttpServlet {
    private ThymeleafTemplateRenderer thymeleafTemplateRenderer;
    private UserService userService;
    private SessionService sessionService;
    private InputValidator inputValidator;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userService = ((UserService) getServletContext().getAttribute("userService"));
        sessionService = ((SessionService) getServletContext().getAttribute("sessionService"));
        thymeleafTemplateRenderer = ((ThymeleafTemplateRenderer) getServletContext().getAttribute("thymeleafTemplateRenderer"));
        inputValidator = ((InputValidator) getServletContext().getAttribute("inputValidator"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        thymeleafTemplateRenderer.renderTemplate(req, resp, "signup");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        User user;
        try {
            inputValidator.validateUserName(parameterMap, "username");
            inputValidator.validatePassword(parameterMap, "password", "password_confirm");
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            user = userService.save(new UserFromRequest(username, password));
            sessionService.get(user, resp);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            thymeleafTemplateRenderer.renderTemplate(req, resp, "signup");
            return;
        }
        resp.sendRedirect("home");
    }
}