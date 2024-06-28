package com.solo83.weatherapp.service;

import com.solo83.weatherapp.dto.GetUserRequest;
import com.solo83.weatherapp.entity.User;
import com.solo83.weatherapp.repository.Repository;
import com.solo83.weatherapp.repository.UserRepository;
import com.solo83.weatherapp.utils.exception.RepositoryException;
import com.solo83.weatherapp.utils.exception.ServiceException;
import org.mindrot.jbcrypt.BCrypt;


public class UserService {

    private static UserService INSTANCE;
    private final Repository<Integer, User> userRepository = UserRepository.getInstance();

    private UserService() {
    }

    public static UserService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new UserService();
        }
        return INSTANCE;
    }

    public void save (GetUserRequest getUserRequest) throws ServiceException {
        String hashPass = BCrypt.hashpw(getUserRequest.getPassword(), BCrypt.gensalt(12));
        User user = new User(getUserRequest.getLogin(),hashPass);

        try {
           userRepository.save(user);
        } catch (RepositoryException e) {
            throw new ServiceException("User already exist");
        }
    }

}
