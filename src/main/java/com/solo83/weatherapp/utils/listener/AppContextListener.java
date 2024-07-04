package com.solo83.weatherapp.utils.listener;

import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.service.SessionService;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import lombok.extern.slf4j.Slf4j;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AppContextListener implements ServletContextListener {

    SessionService sessionService = SessionService.getInstance();


    final Runnable sessionChecker = () -> {

        try {
            for (UserSession session : sessionService.getAll()) {
                if (!sessionService.isSessionValid(session.getExpiresAt())) {
                   String sessionId = session.getId();
                   sessionService.remove(session.getId());
                   log.info("Deleted expired session {}",sessionId);
                }
            }
        } catch (RepositoryException e) {
            log.error("Error while removing session",e);
        }
    };
    private volatile ScheduledExecutorService executor;

    public void contextInitialized(ServletContextEvent sce)
    {
        executor = Executors.newScheduledThreadPool(2);
        executor.scheduleAtFixedRate(sessionChecker, 0, 3, TimeUnit.MINUTES);
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
