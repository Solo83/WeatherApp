package com.solo83.weatherapp.utils.exception;

public class ServiceException extends RuntimeException{
    public ServiceException(String message) {
        super(message);
    }
}
