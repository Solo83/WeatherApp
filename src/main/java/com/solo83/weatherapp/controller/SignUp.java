package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.dto.GetUserRequest;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.service.SessionService;
import com.solo83.weatherapp.service.UserService;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import com.solo83.weatherapp.utils.exception.ServiceException;
import com.solo83.weatherapp.utils.exception.ValidatorException;
import com.solo83.weatherapp.utils.renderer.ThymeleafTemplateRenderer;
import com.solo83.weatherapp.utils.validator.InputValidator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/signup")
public class SignUp extends HttpServlet {

    private final ThymeleafTemplateRenderer thymeleafTemplateRenderer = ThymeleafTemplateRenderer.getInstance();
    private final InputValidator validator = new InputValidator();
    private final UserService userService = UserService.getInstance();
    private final SessionService sessionService = SessionService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        thymeleafTemplateRenderer.renderTemplate(req,resp,"signup");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Map<String, String[]> parameterMap = req.getParameterMap();
        User user;
        try {
            validator.validateUserName(parameterMap,"username");
            validator.validatePassword(parameterMap,"password","password_confirm");

            String username = req.getParameter("username");
            String password = req.getParameter("password");

            user = userService.save(new GetUserRequest(username,password));
            sessionService.getSession(user,resp);

        } catch (ValidatorException | ServiceException | RepositoryException e) {
            req.setAttribute("error", e.getMessage());
            thymeleafTemplateRenderer.renderTemplate(req, resp, "signup");
            return;
        }

        resp.sendRedirect("home");
    }
}
