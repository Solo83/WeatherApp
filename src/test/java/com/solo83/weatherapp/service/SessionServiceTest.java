package com.solo83.weatherapp.service;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.entity.UserSession;

import com.solo83.weatherapp.repository.SessionRepository;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class SessionServiceTest {

    private HttpServletResponse response;

    private final SessionService sessionService = SessionService.getInstance();

    SessionRepository sessionRepository = Mockito.spy(SessionRepository.class);

    private User user;
    private UserSession userSession;


    @BeforeEach
    void setUp() throws RepositoryException {
        user = new User(1, "testUser","testPassword");
        userSession = new UserSession(UUID.randomUUID().toString(), user, LocalDateTime.now().plusMinutes(10));
        response = mock(HttpServletResponse.class);

    }

    @Test
    public void testGetValidSession() throws RepositoryException {

           Mockito.doReturn(Optional.of(userSession)).when(sessionRepository).findByUserId((any(String.class)));
           Optional<UserSession> session = sessionService.get(user, response);

            assertTrue(session.isPresent());
            assertEquals(userSession, session.get());
            verify(sessionRepository, never()).save(any(UserSession.class));
            verify(sessionRepository, times(1)).update(userSession);
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



