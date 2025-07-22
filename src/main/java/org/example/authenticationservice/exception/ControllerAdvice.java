package org.example.authenticationservice.exception;

import org.example.authenticationservice.exception.customExceptions.UserAlreadyExistsException;
import org.example.authenticationservice.exception.customExceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseOnError handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return new ResponseOnError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseOnError handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseOnError(LocalDateTime.now(), HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseOnError handleRuntimeException(RuntimeException e) {
        return new ResponseOnError(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
