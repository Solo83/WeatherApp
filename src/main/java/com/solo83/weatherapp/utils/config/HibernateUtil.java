package com.solo83.weatherapp.utils.config;

import com.solo83.weatherapp.entity.Location;
import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.entity.User;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


import java.io.FileInputStream;
import java.net.URI;
import java.util.Objects;
import java.util.Properties;


@Slf4j
@UtilityClass
public final class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {

            Properties properties = new Properties();

            try {
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                URI uri = Objects.requireNonNull(classloader.getResource("hibernate.properties")).toURI();
                properties.load(new FileInputStream(String.valueOf(uri)));
            } catch (Exception e) {
                log.error("Cannot load properties file", e);
            }

            sessionFactory = new Configuration()
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(UserSession.class)
                    .addAnnotatedClass(Location.class)
                    .addProperties(properties).buildSessionFactory();
        }
        return sessionFactory;
    }
}


