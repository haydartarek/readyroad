package com.readyroad.readyroadbackend.marketing.analytics;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSearchConsoleWorkbookException extends RuntimeException {

    public InvalidSearchConsoleWorkbookException(String message) {
        super(message);
    }

    public InvalidSearchConsoleWorkbookException(String message, Throwable cause) {
        super(message, cause);
    }
}
