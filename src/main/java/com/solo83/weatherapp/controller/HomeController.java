package com.solo83.weatherapp.controller;

import java.io.Writer;
import java.util.Calendar;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

public class HomeController implements ThymeLeafController {

    public HomeController() {
        super();
    }


    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer) {

        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
        ctx.setVariable("today", Calendar.getInstance());
        templateEngine.process("home", ctx, writer);

    }
}
