package com.whoami.launch.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class InvalidTotpException extends RuntimeException {

    public InvalidTotpException(String message) {
        super(message);
    }
    
    
}