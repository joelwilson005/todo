package com.joel.todo.exception;

// Custom exception
public class InvalidTokenException extends Exception {

    // Default constructor for the InvalidTokenException
    public InvalidTokenException() {
    }

    // Constructor that accepts a message, which is passed to the superclass
    // Exception
    public InvalidTokenException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause, which are both passed to the
    // superclass Exception
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause, which is passed to the superclass Exception
    public InvalidTokenException(Throwable cause) {
        super(cause);
    }

    // Constructor that accepts a message, a cause, and two boolean values that
    // determine whether suppression is enabled and whether the stack trace is
    // writable
    public InvalidTokenException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}