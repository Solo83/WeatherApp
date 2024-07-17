package com.solo83.weatherapp.service;

import com.solo83.weatherapp.dto.GetUserRequest;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.repository.SessionRepository;
import com.solo83.weatherapp.repository.UserRepository;
import com.solo83.weatherapp.utils.config.HibernateUtil;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class UserServiceTest {

    private final SessionService sessionService = SessionService.getInstance(SessionRepository.getInstance(HibernateUtil.getSessionFactory()),CookieService.getInstance());
    private final UserService userService = UserService.getInstance(UserRepository.getInstance(HibernateUtil.getSessionFactory()), CookieService.getInstance(), sessionService);

    @Test
    public void testSaveUser() throws RepositoryException {
        GetUserRequest getUserRequest = new GetUserRequest("testUser", "testPassword");
        User savedUser = userService.save(getUserRequest);
        assertEquals("testUser", savedUser.getLogin());
    }

    @Test
    public void testSaveUserThrowsException() {
        GetUserRequest getUserRequest = new GetUserRequest("testUser", "testPassword");
        assertThrows(RepositoryException.class, () -> userService.save(getUserRequest));
    }

}