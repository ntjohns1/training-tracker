package com.noslen.training_tracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates service-layer exceptions into RFC 7807 problem-detail JSON responses.
 * The service layer signals "not found" with RuntimeException whose message contains
 * "not found"; bad input with IllegalArgumentException; and ownership violations with
 * SecurityException.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadRequest(IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ProblemDetail handleForbidden(SecurityException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntime(RuntimeException ex) {
        String message = ex.getMessage();
        if (message != null && message.toLowerCase().contains("not found")) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message);
        }
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                message != null ? message : "Unexpected error");
    }
}
