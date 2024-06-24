package com.solo83.weatherapp.controller;

import com.solo83.weatherapp.service.OpenWeatherApiService;
import com.solo83.weatherapp.utils.exception.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/search")
public class FindLocation extends HttpServlet {

    private final OpenWeatherApiService openWeatherApiService = OpenWeatherApiService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String locationName = req.getParameter("locationName");

        try (PrintWriter printWriter = resp.getWriter()) {
            printWriter.write("<h1> Finded  locations: </h1>");
            printWriter.write("<table>");
            printWriter.write("<tr>");
            printWriter.write("<th>Name</th>");
            printWriter.write("<th>Country</th>");
            printWriter.write("<th>State</th>");
            printWriter.write("<th>latitude</th>");
            printWriter.write("<th>longitude</th>");
            printWriter.write("<th>temperature</th>");
            printWriter.write("</tr>");
            openWeatherApiService.getLocations(locationName).forEach(getLocationRequest -> printWriter.write("""
                    <tr>
                    <td>
                        %s
                    </td>
                    <td>
                        %s
                    </td>
                    <td>
                        %s
                    </td>
                    <td>
                        %s
                    </td>
                    <td>
                        %s
                    </td>
                    <td>
                        %s
                    </td>
                    </tr> \s
                   \s""".formatted(getLocationRequest.getName(),getLocationRequest.getCountry(),getLocationRequest.getState()
            ,getLocationRequest.getLatitude(),getLocationRequest.getLongitude(),getLocationRequest.getTemperature())));
            printWriter.write("</tr>");
            printWriter.write("/<table>");
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
        }
    }
}
