package com.solo83.weatherapp.utils.validator;
import com.solo83.weatherapp.utils.exception.ValidatorException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class InputValidator {

    private final String PASSWORD_PATTERN = "^[a-zA-Z0-9]{4}$";
    private final String USERNAME_PATTERN = "^[a-zA-Z0-9]*$";

    private final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
    private final Pattern usernamePattern = Pattern.compile(USERNAME_PATTERN);

    public void validatePassword(Map<String, String[]> parameterMap, String password, String passwordConfirm) throws ValidatorException {

        String[] passwordValue = parameterMap.get(password);
        String[] confirmationValue = parameterMap.get(passwordConfirm);

        if (passwordValue[0] == null || passwordValue[0].isEmpty()) {
            log.error("Password value is missing");
            throw new ValidatorException("Password is empty");
        }

        if (confirmationValue[0] == null || confirmationValue[0].isEmpty()) {
            log.error("Password confirmation value is missing");
            throw new ValidatorException("Password confirmation is empty");
        }

        String comparedPasswordValue = passwordValue[0];

        if (!passwordPattern.matcher(comparedPasswordValue).find()) {
            log.error("Password does not match pattern");
            throw new ValidatorException("Password does not match pattern");
        }

        if (!confirmationValue[0].equals(passwordValue[0])) {
            log.error("Password mismatch");
            throw new ValidatorException("Password mismatch");
        }

        log.info("Password is VALID");
    }

    public void validateUserName(Map<String, String[]> parameterMap, String userName) throws ValidatorException {

        String[] userNameValue = parameterMap.get(userName);


        if (userNameValue  == null || userNameValue .length == 0) {
            log.error("Username is missing");
            throw new ValidatorException("Username is empty");
        }

        String compareUserNameValue = userNameValue [0];

        if (compareUserNameValue.trim().isEmpty() || !usernamePattern.matcher(compareUserNameValue).find()) {
            log.error("Username does not match pattern");
            throw new ValidatorException("Username does not match pattern");
        }

        log.info("Username is VALID");
    }
}

