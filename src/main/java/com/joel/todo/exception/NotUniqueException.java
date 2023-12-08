package com.joel.todo.exception;


// Custom exception that is thrown when a user attempts to register with an email address that is already in use
public class NotUniqueException extends Exception {

    public NotUniqueException(String message) {
        super(message);
    }

}
