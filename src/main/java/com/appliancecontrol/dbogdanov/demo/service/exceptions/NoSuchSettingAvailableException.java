package com.appliancecontrol.dbogdanov.demo.service.exceptions;

public class NoSuchSettingAvailableException extends RuntimeException {
    public NoSuchSettingAvailableException(String message) {
        super(message);
    }
}
