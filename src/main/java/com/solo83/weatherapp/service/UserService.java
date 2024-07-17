package com.solo83.weatherapp.service;

import com.solo83.weatherapp.dto.GetUserRequest;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.repository.UserRepository;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import com.solo83.weatherapp.utils.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

@Slf4j
public class UserService {

    private static UserService INSTANCE;
    private final CookieService cookieService ;
    private final UserRepository userRepository;
    private final SessionService sessionService;

    private UserService(UserRepository userRepository,CookieService cookieService,SessionService sessionService) {
        this.userRepository = userRepository;
        this.cookieService = cookieService;
        this.sessionService = sessionService;
    }

    public static UserService getInstance(UserRepository userRepository,CookieService cookieService,SessionService sessionService) {
        if (INSTANCE == null) {
            INSTANCE = new UserService(userRepository,cookieService,sessionService);
        }
        return INSTANCE;
    }

    public User getUserFromRequest(HttpServletRequest req) throws ServiceException {
        User user = (User) req.getAttribute("user");

        if (user == null) {
            Optional<Cookie> cookie = cookieService.get(req);
            if (cookie.isEmpty()) {
                throw new ServiceException("Cookie not found");
            }

            String sessionId = cookie.get().getValue();
            Optional<UserSession> session = sessionService.getById(sessionId);
            if (session.isEmpty()) {
                throw new ServiceException("Session not found");
            }
            user = session.get().getUser();
        }

        return user;
    }


    public User save(GetUserRequest getUserRequest) throws RepositoryException {
        String hashPass = BCrypt.hashpw(getUserRequest.getPassword(), BCrypt.gensalt(12));
        User user = new User(getUserRequest.getLogin(), hashPass);
        Optional<User> userOptional;
        try {
            userOptional = userRepository.save(user);
        } catch (RepositoryException e) {
            throw new RepositoryException("User cant be saved");
        }

        return userOptional.get();

    }

    public User getUser(GetUserRequest getUserRequest) throws ServiceException, RepositoryException {
        User user;
        try {
            user = userRepository.findByUserName(getUserRequest.getLogin()).get();
        } catch (RepositoryException e) {
            throw new RepositoryException("User does not exist");
        }

        String password = getUserRequest.getPassword();
        String hashPass = user.getPassword();

        if (!isPasswordCorrect(password, hashPass)) {
            throw new ServiceException("Wrong password");
        }

        log.info("Current user: {}", user.getLogin());

        return user;

    }


    public Optional<User> getUserByLocationId(String locationId) throws RepositoryException {
        Optional<User> user;
        try {
            user = userRepository.findByLocationId(locationId);
        } catch (RepositoryException e) {
            throw new RepositoryException("User does not exist");
        }

        return user;
    }

    private boolean isPasswordCorrect(String password, String hashPass) {
        return BCrypt.checkpw(password, hashPass);
    }


}
