package com.solo83.weatherapp.utils.renderer;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

@Slf4j
public class ThymeleafTemplateRenderer extends HttpServlet {
    
    private static ThymeleafTemplateRenderer INSTANCE;
    
        private ThymeleafTemplateRenderer() {
        }
        
        public static ThymeleafTemplateRenderer getInstance() {
            if(INSTANCE == null) {
                INSTANCE = new ThymeleafTemplateRenderer();
            }
            return INSTANCE;
        }

    public void renderTemplate(HttpServletRequest req, HttpServletResponse resp, String templateName) {

        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(req.getServletContext());
        ITemplateEngine templateEngine = buildTemplateEngine(application);
        IWebExchange webExchange = application.buildExchange(req, resp);
        WebContext webContext = new WebContext(webExchange, webExchange.getLocale());
        try {
            templateEngine.process(templateName, webContext, resp.getWriter());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }


    private ITemplateEngine buildTemplateEngine(final IWebApplication application) {

        final WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);
        // HTML is the default mode, but we will set it anyway for better understanding of code
        templateResolver.setTemplateMode(TemplateMode.HTML);
        // This will convert "home" to "/WEB-INF/templates/home.html"
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        // Set template cache TTL to 1 hour. If not set, entries would live in cache until expelled by LRU
        templateResolver.setCacheTTLMs(3600000L);
        // Cache is set to true by default. Set to false if you want templates to
        // be automatically updated when modified.
        templateResolver.setCacheable(false);
        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;

    }
    
}
