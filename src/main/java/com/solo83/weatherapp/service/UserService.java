package com.solo83.weatherapp.service;

import com.solo83.weatherapp.dto.UserFromRequest;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.entity.UserSession;
import com.solo83.weatherapp.repository.UserRepository;
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

    public User getUserFromRequest(HttpServletRequest req) {
        User user = (User) req.getAttribute("user");

        if (user == null) {
            Optional<Cookie> cookie = cookieService.get(req);
            if (cookie.isEmpty()) {
                log.error("Cookie not found");
                return user;
            }
            String sessionId = cookie.get().getValue();
            Optional<UserSession> session = sessionService.getById(sessionId);
            if (session.isEmpty()) {
                log.error("Session not found");
                return user;
            }
            user = session.get().getUser();
        }
        return user;
    }

    public User save(UserFromRequest userFromRequest) {
        String hashPass = BCrypt.hashpw(userFromRequest.getPassword(), BCrypt.gensalt(12));
        User user = new User(userFromRequest.getLogin(), hashPass);
        Optional<User> userOptional;
        try {
            userOptional = userRepository.save(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceException("User can't be saved (already exists)");
        }
        return userOptional.get();
    }

    public User getUser(UserFromRequest userFromRequest) {
        User user;
        try {
            user = userRepository.findByUserName(userFromRequest.getLogin()).get();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceException("User does not exist (getUser from request error)");
        }
        String password = userFromRequest.getPassword();
        String hashPass = user.getPassword();
        if (!isPasswordCorrect(password, hashPass)) {
            throw new ServiceException("Wrong password");
        }
        log.info("Current user: {}", user.getLogin());
        return user;
    }

    public Optional<User> getUserByLocationId(String locationId,String userId) {
        Optional<User> user;
        try {
            user = userRepository.findByLocationId(locationId,userId);
        } catch (Exception e) {
            throw new ServiceException("getUserByLocationId error");
        }
        return user;
    }

    private boolean isPasswordCorrect(String password, String hashPass) {
        return BCrypt.checkpw(password, hashPass);
    }

    public void update(User user) {
        userRepository.update(user);
    }
}
