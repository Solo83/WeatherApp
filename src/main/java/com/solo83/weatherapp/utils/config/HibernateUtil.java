package com.solo83.weatherapp.utils.config;

import com.solo83.weatherapp.entity.Location;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.entity.UserSession;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

@Slf4j
@UtilityClass
public class HibernateUtil {
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Properties properties = new Properties();
            try {
                properties.load(HibernateUtil.class.getClassLoader().getResourceAsStream("hibernate.properties"));
            } catch (Exception e) {
                log.error("Cannot load properties file", e);
            }

            sessionFactory = new Configuration()
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(UserSession.class)
                    .addAnnotatedClass(Location.class)
                    .addProperties(properties)
                    .buildSessionFactory();
        }
        return sessionFactory;
    }
}


