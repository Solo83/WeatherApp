package com.solo83.weatherapp.controller;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

import java.io.Writer;

public class Register implements ThymeLeafController {
    @Override
    public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {

        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
        templateEngine.process("register", ctx, writer);
    }
}
