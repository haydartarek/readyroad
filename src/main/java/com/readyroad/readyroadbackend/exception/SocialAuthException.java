package com.readyroad.readyroadbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SocialAuthException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public SocialAuthException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
}
