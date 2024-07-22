package com.solo83.weatherapp.service;

import com.solo83.weatherapp.dto.GetUserRequest;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.repository.SessionRepository;
import com.solo83.weatherapp.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.config.HibernateTestUtil;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class SessionServiceTest {

    private HttpServletResponse response;
    private final SessionService sessionService = SessionService.getInstance(SessionRepository.getInstance(HibernateTestUtil.getSessionFactory()), CookieService.getInstance());
    private final UserService userService = UserService.getInstance(UserRepository.getInstance(HibernateTestUtil.getSessionFactory()), CookieService.getInstance(), sessionService);

    private User user;
    private UserSession userSession;

    @BeforeEach
    void setUp() {
        user = new User(1, "testUser", "testPassword");
        userSession = new UserSession(UUID.randomUUID().toString(), user, LocalDateTime.now().plusMinutes(10));
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void testGetValidSession() {

        GetUserRequest getUserRequest = new GetUserRequest("testNewUser", "testNewPassword");
        User savedUser = userService.save(getUserRequest);

        Optional<UserSession> session = sessionService.get(savedUser, response);

        assertTrue(session.isPresent());
        assertEquals(savedUser, session.get().getUser());
        assertTrue(sessionService.isSessionValid(session.get().getExpiresAt()));
        verify(response, times(1)).addCookie(any(Cookie.class));
    }


    @Test
    void testIsSessionValid() {
        LocalDateTime validTime = LocalDateTime.now().plusMinutes(5);
        LocalDateTime invalidTime = LocalDateTime.now().minusMinutes(5);

        assertTrue(sessionService.isSessionValid(validTime));
        assertFalse(sessionService.isSessionValid(invalidTime));
    }

    @Test
    void testSetSessionExpirationTime() {
        sessionService.setSessionExpirationTime(userSession);
        assertTrue(userSession.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    void testCreateSession() {
        UserSession session = sessionService.create(user);

        assertNotNull(session);
        assertEquals(user, session.getUser());
        assertTrue(session.getExpiresAt().isAfter(LocalDateTime.now()));
    }
}



