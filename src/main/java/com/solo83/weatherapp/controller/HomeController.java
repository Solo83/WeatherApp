package com.solo83.weatherapp.controller;

import java.io.Writer;
import java.util.Calendar;

import com.solo83.weatherapp.dto.GetUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

@Slf4j
public class HomeController implements ThymeLeafController {

    public HomeController() {
        super();
    }


    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer) {

        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
        ctx.setVariable("today", Calendar.getInstance());
        ctx.setVariable("user", ctx.getExchange().getSession().getAttributeValue("user"));
        GetUserRequest getUserRequest = (GetUserRequest)ctx.getExchange().getSession().getAttributeValue("getUserRequest");
        log.info("getUserRequest: {}", getUserRequest);

        templateEngine.process("home", ctx, writer);

    }
}
