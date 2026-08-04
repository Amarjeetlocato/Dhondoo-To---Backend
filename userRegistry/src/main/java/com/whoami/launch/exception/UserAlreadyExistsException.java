package com.whoami.launch.exception;

public class UserAlreadyExistsException
        extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}