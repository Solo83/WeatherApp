package com.solo83.weatherapp.utils.config;

import com.solo83.weatherapp.entity.Location;
import com.solo83.weatherapp.entity.Session;
import com.solo83.weatherapp.entity.User;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.schema.Action;


@Slf4j
@UtilityClass
public final class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration().addAnnotatedClass(User.class).addAnnotatedClass(Session.class).addAnnotatedClass(Location.class)
                    // PostgreSQL
                    .setProperty(AvailableSettings.JAKARTA_JDBC_URL, "jdbc:postgresql://localhost:5432/WeatherApp")
                    // Credentials
                    .setProperty(AvailableSettings.JAKARTA_JDBC_USER, "postgres").setProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD, "123")
                    // Automatic schema export
                    .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.ACTION_UPDATE)
                    // SQL statement logging
                    .setProperty(AvailableSettings.SHOW_SQL, String.valueOf(true)).setProperty(AvailableSettings.FORMAT_SQL, String.valueOf(true)).setProperty(AvailableSettings.HIGHLIGHT_SQL, String.valueOf(true))
                    // Create a new SessionFactory
                    .buildSessionFactory();
        }
        return sessionFactory;
    }
}


