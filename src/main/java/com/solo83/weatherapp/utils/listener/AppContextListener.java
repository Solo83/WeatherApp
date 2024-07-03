package com.solo83.weatherapp.utils.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AppContextListener implements ServletContextListener {

    static ServletContext context;

    final Runnable cookieChecker = () -> {
        System.out.println(context.getAttribute("session"));
        //System.out.println("hello world");
    };
    private volatile ScheduledExecutorService executor;

    public void contextInitialized(ServletContextEvent sce)
    {
        context = sce.getServletContext();
        executor = Executors.newScheduledThreadPool(2);
        executor.scheduleAtFixedRate(cookieChecker, 0, 3, TimeUnit.SECONDS);
    }

    public void contextDestroyed(ServletContextEvent sce)
    {
        final ScheduledExecutorService executor = this.executor;

        if (executor != null)
        {
            executor.shutdown();
            this.executor = null;
        }
    }
}
