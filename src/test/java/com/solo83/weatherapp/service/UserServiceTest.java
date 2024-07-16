package com.solo83.weatherapp.service;

import com.solo83.weatherapp.dto.GetUserRequest;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.utils.config.HibernateUtil;
import com.solo83.weatherapp.utils.exception.RepositoryException;


import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import utils.config.HibernateTestUtil;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

public class UserServiceTest {

    UserService userService = UserService.getInstance();
    static MockedStatic<HibernateUtil> mockedHibernateUtil;

    @BeforeAll
    static void setUpBeforeClass(){
        mockedHibernateUtil = mockStatic(HibernateUtil.class);
        SessionFactory sessionFactory = HibernateTestUtil.getSessionFactory();
        mockedHibernateUtil.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);

    }

    @AfterAll
    static void tearDownAfterClass(){
        mockedHibernateUtil.close();
    }

    @Test
    public void testSaveUser() throws RepositoryException {

        GetUserRequest getUserRequest = new GetUserRequest();
        getUserRequest.setLogin("testUser");
        getUserRequest.setPassword("testPassword");
        User savedUser = userService.save(getUserRequest);
        assertEquals("testUser", savedUser.getLogin());

    }

    @Test
    public void testSaveUserThrowsException() {
        GetUserRequest getUserRequest = new GetUserRequest("testUser", "testPassword");
        assertThrows(RepositoryException.class, () -> userService.save(getUserRequest));
    }

}