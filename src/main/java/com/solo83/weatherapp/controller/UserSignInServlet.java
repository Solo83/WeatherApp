package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.dto.UserFromRequest;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.service.SessionService;
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

@Slf4j
@WebServlet("/signin")
public class UserSignInServlet extends HttpServlet {
    private ThymeleafTemplateRenderer thymeleafTemplateRenderer;
    private UserService userService;
    private SessionService sessionService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userService = ((UserService) getServletContext().getAttribute("userService"));
        sessionService = ((SessionService) getServletContext().getAttribute("sessionService"));
        thymeleafTemplateRenderer = ((ThymeleafTemplateRenderer) getServletContext().getAttribute("thymeleafTemplateRenderer"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        thymeleafTemplateRenderer.renderTemplate(req,resp,"signin");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
       String username = req.getParameter("username");
       String password = req.getParameter("password");
       User user;
        try {
            user = userService.getUser(new UserFromRequest(username, password));
            sessionService.get(user,resp).get();

        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            thymeleafTemplateRenderer.renderTemplate(req, resp, "signin");
            return;
        }
        resp.sendRedirect("home");
    }
}
