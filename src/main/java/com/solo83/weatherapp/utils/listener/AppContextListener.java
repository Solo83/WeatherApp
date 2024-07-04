package com.solo83.weatherapp.utils.listener;

import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.service.SessionPersistanceService;
import com.solo83.weatherapp.service.SessionService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AppContextListener implements ServletContextListener {

    SessionService sessionService = SessionService.getInstance();
    SessionPersistanceService sessionPersistanceService = SessionPersistanceService.getInstance();

    final Runnable sessionChecker = () -> {

        Map<String, UserSession> sessions = SessionPersistanceService.getSessions();
        for (UserSession session : sessions.values()) {
            if (!sessionService.isSessionValid(session.getExpiresAt())) {
                sessionPersistanceService.removeSession(session.getId());
            }
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
