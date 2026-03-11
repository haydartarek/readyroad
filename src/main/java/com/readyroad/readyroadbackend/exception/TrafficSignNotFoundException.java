package com.readyroad.readyroadbackend.exception;

/**
 * Exception thrown when a traffic sign is not found by sign code.
 */
public class TrafficSignNotFoundException extends RuntimeException {

    public TrafficSignNotFoundException(String signCode) {
        super("Traffic sign not found: " + signCode);
    }
}
