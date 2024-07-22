package com.solo83.weatherapp.service;

import com.solo83.weatherapp.dto.UserFromRequest;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.repository.SessionRepository;
import com.solo83.weatherapp.repository.UserRepository;
import com.solo83.weatherapp.utils.exception.ServiceException;
import org.junit.jupiter.api.Test;
import utils.config.HibernateTestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class UserServiceTest {

    private final SessionService sessionService = SessionService.getInstance(SessionRepository.getInstance(HibernateTestUtil.getSessionFactory()),CookieService.getInstance());
    private final UserService userService = UserService.getInstance(UserRepository.getInstance(HibernateTestUtil.getSessionFactory()), CookieService.getInstance(), sessionService);

    @Test
    public void testSaveUser() {
        UserFromRequest userFromRequest = new UserFromRequest("testUser", "testPassword");
        User savedUser = userService.save(userFromRequest);
        assertEquals("testUser", savedUser.getLogin());
    }

    @Test
    public void testSaveUserThrowsException() {
        UserFromRequest userFromRequest = new UserFromRequest("testUser", "testPassword");
        assertThrows(ServiceException.class, () -> userService.save(userFromRequest));
    }

}