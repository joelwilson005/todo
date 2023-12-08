package com.joel.todo.advice;

import com.joel.todo.exception.ApiError;
import com.joel.todo.exception.ErrorTracker;
import com.joel.todo.exception.NotUniqueException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
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
// This annotation makes this class a central point for exception handling across all @RequestMapping methods
public class ApplicationExceptionController {

    // This method creates a new ApiError object, sets the current timestamp and the provided message, and returns it wrapped in a ResponseEntity with the provided status
    private ResponseEntity<ApiError> handleException(HttpStatus status, String message) {
        ApiError error = new ApiError();
        error.setTimestamp(new Timestamp(System.currentTimeMillis()));
        error.setMessage(message);
        return new ResponseEntity<>(error, status);
    }

    // This method handles NoSuchElementExceptions, returning a 404 status and a custom error message
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> noSuchElementExceptionHandler(NoSuchElementException e) {
        return handleException(HttpStatus.NOT_FOUND, "The requested entity could not be found.");
    }

    // This method handles AuthenticationExceptions and BadCredentialsExceptions, returning a 401 status and a custom error message
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiError> authenticationExceptionHandler(AuthenticationException e) {
        return handleException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    // This method handles NotUniqueExceptions, returning a 400 status and a custom error message
    @ExceptionHandler(NotUniqueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> notUniqueExceptionHandler(NotUniqueException e) {
        return handleException(HttpStatus.BAD_REQUEST, "Invalid request: the provided data is not unique. Please ensure that the request is properly formatted and try again.");
    }

    // This method handles MalformedURLExceptions, returning a 400 status and a custom error message
    @ExceptionHandler(MalformedURLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> malformedURLExceptionHandler(MalformedURLException e) {
        return handleException(HttpStatus.BAD_REQUEST, "Invalid URL");
    }

    // This method handles MethodArgumentTypeMismatchExceptions, returning a 400 status and a custom error message
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> methodArgumentMistMatchExceptionHandler(MethodArgumentTypeMismatchException e) {
        return handleException(HttpStatus.BAD_REQUEST, "Invalid URL");
    }

    // This method handles MethodArgumentNotValidExceptions, returning a 400 status and a custom error message
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        return handleException(HttpStatus.BAD_REQUEST, "Invalid request body");
    }

    // This method handles ValidationExceptions, returning a 400 status and a custom error message
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> validationExceptionHandler(ValidationException e) {
        return handleException(HttpStatus.BAD_REQUEST, "Invalid data");
    }

    // This method handles AccessDeniedExceptions, returning a 403 status and a custom error message
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiError> accessDeniedHandler(AccessDeniedException e) {
        return handleException(HttpStatus.FORBIDDEN, "Forbidden");
    }

    // This method handles HttpMediaTypeNotSupportedExceptions, returning a 400 status and a custom error message
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> httpMediaTypeNotSupportedHandler(HttpMediaTypeNotSupportedException e) {
        return handleException(HttpStatus.BAD_REQUEST, "Media type not supported");
    }

    // This method handles HttpRequestMethodNotSupportedExceptions, returning a 405 status and a custom error message
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<ApiError> httpRequestMethodNotSupportedHandler(HttpRequestMethodNotSupportedException e) {
        return handleException(HttpStatus.METHOD_NOT_ALLOWED, "Not allowed");
    }

    // This method handles IndexOutOfBoundsExceptions, returning a 400 status and a custom error message
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> indexOutOfBoundsHandler(IndexOutOfBoundsException e) {
        return handleException(HttpStatus.BAD_REQUEST, "Index out of bounds");
    }

    // This method handles UsernameNotFoundExceptions, returning a 404 status and a custom error message
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> userNameNotFoundHandler(UsernameNotFoundException e) {
        return handleException(HttpStatus.NOT_FOUND, "User not found");
    }

    // This method handles all other Exceptions, returning a 500 status and a custom error message
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> defaultExceptionHandler(Exception e) {
        ErrorTracker.reportError(e);
        return handleException(HttpStatus.INTERNAL_SERVER_ERROR, "An error has occurred.");
    }
}