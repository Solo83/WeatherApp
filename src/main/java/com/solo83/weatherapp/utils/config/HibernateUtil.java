package com.solo83.weatherapp.utils.config;

import com.solo83.weatherapp.entity.Location;
import com.solo83.weatherapp.entity.Session;
import com.solo83.weatherapp.entity.User;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


import java.io.FileInputStream;
import java.util.Properties;


@Slf4j
@UtilityClass
public final class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {

            Properties properties = new Properties();

            try {
                properties.load(new FileInputStream("hibernate.properties"));
            } catch (Exception e) {
                log.error("Cannot load properties file", e);
            }

            sessionFactory = new Configuration().addAnnotatedClass(User.class).addAnnotatedClass(Session.class).addAnnotatedClass(Location.class).mergeProperties(properties).buildSessionFactory();
        }
        return sessionFactory;
    }
}


