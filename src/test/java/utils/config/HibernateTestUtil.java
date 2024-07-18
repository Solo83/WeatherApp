package utils.config;

import com.solo83.weatherapp.entity.Location;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.entity.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


import java.util.Properties;

@Slf4j
public class HibernateTestUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Properties properties = new Properties();
            try {
                properties.load(HibernateTestUtil.class.getClassLoader().getResourceAsStream("test.properties"));
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
