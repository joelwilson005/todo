package com.joel.todo.exception;

import io.sentry.Sentry;
import org.springframework.stereotype.Service;

@Service
public class ErrorTracker {

    // Sends unhandled errors to Sentry
    // This method is used to report unhandled errors to Sentry. It takes an
    // exception as an argument.
    public static void reportError(Throwable throwable) {
        // captureException method of Sentry class is used to send the details of the
        // exception to Sentry.
        Sentry.captureException(throwable);
    }

}
