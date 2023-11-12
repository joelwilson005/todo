package com.joel.todo;

import com.joel.todo.exception.ErrorTracker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class TodoApplication {

    public static void main(String[] args) {
        try {

            SpringApplication.run(TodoApplication.class, args);

        } catch (Throwable throwable) {

            ErrorTracker.reportError(throwable); // Send any unhandled exceptions to Sentry.

        }
    }


}
