package com.readyroad.readyroadbackend.marketing.analytics;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidSearchConsoleWorkbookException extends ResponseStatusException {

    public InvalidSearchConsoleWorkbookException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public InvalidSearchConsoleWorkbookException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }
}
