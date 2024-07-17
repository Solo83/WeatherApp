package com.solo83.weatherapp.utils.listener;

import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.repository.SessionRepository;
import com.solo83.weatherapp.service.CookieService;
import com.solo83.weatherapp.service.SessionService;
import com.solo83.weatherapp.utils.config.HibernateUtil;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AppContextListener implements ServletContextListener {

    private final SessionService sessionService = SessionService.getInstance(SessionRepository.getInstance(HibernateUtil.getSessionFactory()), CookieService.getInstance());

    final Runnable sessionChecker = () -> {

        try {
            for (UserSession session : sessionService.getAll()) {
                if (!sessionService.isSessionValid(session.getExpiresAt())) {
                    String sessionId = session.getId();
                    log.info("Expired session will be deleted");
                    sessionService.remove(sessionId);
                }
            }
        } catch (RepositoryException e) {
            log.error("Error while removing session", e);
        }
    };
    private volatile ScheduledExecutorService executor;

    public void contextInitialized(ServletContextEvent sce) {
        executor = Executors.newScheduledThreadPool(2);
        executor.scheduleAtFixedRate(sessionChecker, 0, 3, TimeUnit.MINUTES);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        final ScheduledExecutorService executor = this.executor;

        if (executor != null) {
            executor.shutdown();
            this.executor = null;
        }
    }
}
