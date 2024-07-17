package com.solo83.weatherapp.service;

import com.solo83.weatherapp.dto.GetUserRequest;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.utils.config.HibernateUtil;
import com.solo83.weatherapp.utils.exception.RepositoryException;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import utils.config.HibernateTestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

class UserServiceTest {

    UserService userService = UserService.TEST_CreateInstance();

    @Test
    public void testSaveUser() throws RepositoryException {


        try (MockedStatic<HibernateUtil> mockedHibernateUtil = mockStatic(HibernateUtil.class)) {
            SessionFactory sessionFactory = HibernateTestUtil.getSessionFactory();
            mockedHibernateUtil.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);

            GetUserRequest getUserRequest = new GetUserRequest();
            getUserRequest.setLogin("testUser");
            getUserRequest.setPassword("testPassword");
            User savedUser = userService.save(getUserRequest);
            assertEquals("testUser", savedUser.getLogin());

        }
    }

    @Test
    public void testSaveUserThrowsException() {

        try (MockedStatic<HibernateUtil> mockedHibernateUtil = mockStatic(HibernateUtil.class)) {
            SessionFactory sessionFactory = HibernateTestUtil.getSessionFactory();
            mockedHibernateUtil.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);

            GetUserRequest getUserRequest = new GetUserRequest("testUser", "testPassword");
            assertThrows(RepositoryException.class, () -> userService.save(getUserRequest));
        }
    }

}