package com.joel.todo.advice;

import com.joel.todo.exception.ApiError;
import com.joel.todo.exception.ErrorTracker;
import com.joel.todo.exception.NotUniqueException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApplicationExceptionController {

    private ResponseEntity<ApiError> handleException(HttpStatus status, String message) {

        ApiError error = new ApiError();
        error.setTimestamp(new Timestamp(System.currentTimeMillis()));
        error.setMessage(message);

        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> noSuchElementExceptionHandler(NoSuchElementException e) {

        return handleException(HttpStatus.NOT_FOUND, "The requested entity could not be found.");
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiError> authenticationExceptionHandler(AuthenticationException e) {

        return handleException(HttpStatus.UNAUTHORIZED, "Unauthorized");

    }

    @ExceptionHandler(NotUniqueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> notUniqueExceptionHandler(NotUniqueException e) {

        return handleException(HttpStatus.BAD_REQUEST, "Invalid request: the provided data is not unique. Please ensure that the request is properly formatted and try again.");

    }

    @ExceptionHandler(MalformedURLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> malformedURLExceptionHandler(MalformedURLException e) {

        return handleException(HttpStatus.BAD_REQUEST, "Invalid URL");

    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> methodArgumentMistMatchExceptionHandler(MethodArgumentTypeMismatchException e) {

        return handleException(HttpStatus.BAD_REQUEST, "Invalid URL");

    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {

        return handleException(HttpStatus.BAD_REQUEST, "Invalid request body");

    }


    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> validationExceptionHandler(ValidationException e) {

        return handleException(HttpStatus.BAD_REQUEST, "Invalid data");

    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiError> accessDeniedHandler(AccessDeniedException e) {

        return handleException(HttpStatus.FORBIDDEN, "Forbidden");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> httpMediaTypeNotSupportedHandler(HttpMediaTypeNotSupportedException e) {

        return handleException(HttpStatus.BAD_REQUEST, "Media type not supported");

    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<ApiError> httpRequestMethodNotSupportedHandler(HttpRequestMethodNotSupportedException e) {

        return handleException(HttpStatus.METHOD_NOT_ALLOWED, "Not allowed");

    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> indexOutOfBoundsHandler(IndexOutOfBoundsException e) {

        return handleException(HttpStatus.BAD_REQUEST, "Index out of bounds");

    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> userNameNotFoundHandler(UsernameNotFoundException e) {

        return handleException(HttpStatus.NOT_FOUND, "User not found");

    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> defaultExceptionHandler(Exception e) {

        ErrorTracker.reportError(e);

        return handleException(HttpStatus.INTERNAL_SERVER_ERROR, "An error has occurred.");

    }

}