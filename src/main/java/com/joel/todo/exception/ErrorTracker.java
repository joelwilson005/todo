package com.joel.todo.exception;

import io.sentry.Sentry;
import org.springframework.stereotype.Service;

@Service
public class ErrorTracker {

    // Sends unhandled errors to Sentry
    public static void reportError(Throwable throwable) {
        Sentry.captureException(throwable);
    }

}
