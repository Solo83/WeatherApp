package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.dto.GetUserRequest;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.service.CookieService;
import com.solo83.weatherapp.service.UserService;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import com.solo83.weatherapp.utils.exception.ServiceException;
import com.solo83.weatherapp.utils.renderer.ThymeleafTemplateRenderer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@WebServlet("/signin")
public class SignIn extends HttpServlet {
    private final UserService userService = UserService.getInstance();
    private final CookieService cookieService = CookieService.getInstance();
    private final ThymeleafTemplateRenderer thymeleafTemplateRenderer = ThymeleafTemplateRenderer.getInstance();


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username.isEmpty() || password.isEmpty()) {
            req.setAttribute("error", "Enter username and password");
            thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
            return;
        }

       User user;

        try {
            user = userService.getUser(new GetUserRequest(username, password));
            resp = cookieService.setCookie(resp,user);
            getServletContext().setAttribute("LOGGED_USER", user.getLogin());

        } catch (ServiceException | RepositoryException e) {
            req.setAttribute("error", e.getMessage());
            thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
            return;
        }

       // UserSession session = SessionService.getSessions().get(user.getId().toString());
       // ServletContext servletContext = req.getServletContext();
       // servletContext.setAttribute("activeUser",user);


       /* try (Writer writer = resp.getWriter()) {
            writer.write("<HTML><HEAD><h3>Hi " + user.getLogin() + "</h3></HEAD><BODY>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/


       thymeleafTemplateRenderer.renderTemplate(req, resp, "home");
    }
}
