package com.appliancecontrol.dbogdanov.demo.service.exceptions;

public class NoSuchStateValueAvailableException extends RuntimeException {
    public NoSuchStateValueAvailableException(String message) {
        super(message);
    }
}
