package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.service.SessionPersistanceService;
import com.solo83.weatherapp.utils.renderer.ThymeleafTemplateRenderer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/home")
public class Home extends HttpServlet {

    ThymeleafTemplateRenderer thymeleafTemplateRenderer = ThymeleafTemplateRenderer.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
        getServletContext().setAttribute("LOGGED_USER", getServletContext().getAttribute(("LOGGED_USER")));
        log.info(String.valueOf(getServletContext().getAttribute(("LOGGED_USER"))));
        log.info(SessionPersistanceService.getSessions().toString());
        thymeleafTemplateRenderer.renderTemplate(req,resp,"home");
    }
}
