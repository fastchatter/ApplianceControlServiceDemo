package com.appliancecontrol.dbogdanov.demo.service.exceptions;

public class NoSuchSettingValueAvailableException extends RuntimeException {
    public NoSuchSettingValueAvailableException(String message) {
        super(message);
    }
}
