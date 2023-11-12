package com.joel.todo.exception;

public class NotUniqueException extends Exception{

    public NotUniqueException(String message) {
        super(message);
    }

    public NotUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotUniqueException(Throwable cause) {
        super(cause);
    }

    public NotUniqueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);

    }
}
