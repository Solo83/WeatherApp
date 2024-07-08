package com.solo83.weatherapp.service;

import com.solo83.weatherapp.dto.GetUserRequest;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.repository.UserRepository;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import com.solo83.weatherapp.utils.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

@Slf4j
public class UserService {

    private static UserService INSTANCE;
    private final UserRepository userRepository = UserRepository.getInstance();

    private UserService() {
    }

    public static UserService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserService();
        }
        return INSTANCE;
    }

    public void save(GetUserRequest getUserRequest) throws ServiceException {
        String hashPass = BCrypt.hashpw(getUserRequest.getPassword(), BCrypt.gensalt(12));
        User user = new User(getUserRequest.getLogin(), hashPass);

        try {
            userRepository.save(user);
        } catch (RepositoryException e) {
            throw new ServiceException("User already exist");
        }
    }

    public User getUser(GetUserRequest getUserRequest) throws ServiceException {
        User user;
        try {
            user = userRepository.findByUserName(getUserRequest.getLogin()).get();
        } catch (RepositoryException e) {
            throw new ServiceException("User does not exist");
        }

        String password = getUserRequest.getPassword();
        String hashPass = user.getPassword();

        if (!isPasswordCorrect(password, hashPass)) {
            throw new ServiceException("Wrong password");
        }

        log.info("Current user: {}", user.getLogin());

        return user;

    }

    Optional<User> getUser(Integer id) throws ServiceException {

        Optional<User> user;

        try {
            user = userRepository.findById(id);
        } catch (RepositoryException e) {
            throw new ServiceException("User does not exist");
        }

        return user;

    }


    private boolean isPasswordCorrect(String password, String hashPass) {
        return BCrypt.checkpw(password, hashPass);
    }


}
