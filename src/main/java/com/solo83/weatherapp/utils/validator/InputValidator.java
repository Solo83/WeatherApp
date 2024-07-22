package com.solo83.weatherapp.utils.validator;

import com.solo83.weatherapp.utils.exception.ValidatorException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class InputValidator {
    private final String USERNAME_PATTERN = "^[a-zA-Z0-9]*$";
    private final String PASSWORD_PATTERN = "^[a-zA-Z0-9]*$";
    private final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
    private final Pattern usernamePattern = Pattern.compile(USERNAME_PATTERN);
    private static InputValidator INSTANCE;

    private InputValidator() {
    }

    public static InputValidator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InputValidator();
        }
        return INSTANCE;
    }

    public void validatePassword(Map<String, String[]> parameterMap, String password, String passwordConfirm) {
        String passwordValue = parameterMap.get(password)[0];
        String confirmationValue = parameterMap.get(passwordConfirm)[0];
        if (passwordValue == null || passwordValue.isEmpty()) {
            log.error("Password value is missing");
            throw new ValidatorException("Password is empty");
        }
        if (!passwordPattern.matcher(passwordValue).find()) {
            log.error("Password should contain 4 letters or numbers, without spaces");
            throw new ValidatorException("Password must contain only numbers and letters");
        }
        if (confirmationValue == null || confirmationValue.isEmpty()) {
            log.error("Password confirmation value is missing");
            throw new ValidatorException("Password confirmation is empty");
        }
        if (!confirmationValue.equals(passwordValue)) {
            log.error("Password and confirmation does not match");
            throw new ValidatorException("Password and confirmation does not match");
        }
        log.info("Password is VALID");
    }

    public void validateUserName(Map<String, String[]> parameterMap, String userName) {
        String userNameValue = parameterMap.get(userName)[0];
        if (userNameValue == null || userNameValue.isEmpty()) {
            log.error("Username is missing");
            throw new ValidatorException("Username is empty");
        }
        if (userNameValue.trim().isEmpty() || !usernamePattern.matcher(userNameValue).find()) {
            log.error("Username must contain only numbers and letters");
            throw new ValidatorException("Username must contain only numbers and letters");
        }
        log.info("Username is VALID");
    }
}

