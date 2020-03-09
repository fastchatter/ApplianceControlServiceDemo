package com.appliancecontrol.dbogdanov.demo.service.exceptions;

public class ApplianceNotFoundException extends RuntimeException {
    public ApplianceNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
