package com.solo83.weatherapp.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/search")
public class HomeSearch extends Search {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        processRequest(req, resp, "search","home");
    }


}

