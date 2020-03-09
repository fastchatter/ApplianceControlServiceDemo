package com.appliancecontrol.dbogdanov.demo.controller.advice;

import com.appliancecontrol.dbogdanov.demo.service.exceptions.ApplianceNotFoundException;
import com.appliancecontrol.dbogdanov.demo.service.exceptions.NoSuchSettingAvailableException;
import com.appliancecontrol.dbogdanov.demo.service.exceptions.NoSuchSettingValueAvailableException;
import com.appliancecontrol.dbogdanov.demo.service.exceptions.NoSuchStateValueAvailableException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;


@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApplianceNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleApplianceNotFoundException(ApplianceNotFoundException ex) {
        return new ResponseEntity<>(
                new ErrorResponse("no-appliance-found", "No appliance found"),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({
            NoSuchSettingAvailableException.class,
            NoSuchSettingValueAvailableException.class,
            NoSuchStateValueAvailableException.class,
    })
    protected ResponseEntity<ErrorResponse> handleNoSuchValueException(RuntimeException ex) {
        log.error("bad-request-value-error", ex);
        return new ResponseEntity<>(
                new ErrorResponse("bad-request-value", ex.getLocalizedMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorResponse> handleInternalError(RuntimeException ex) {
        log.error("internal-error", ex);
        return new ResponseEntity<>(
                new ErrorResponse("internal-server-error", "Internal server error"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        log.error("invalid-argument", ex);
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        return new ResponseEntity<>(
                new ErrorResponse("bad-request-value", String.join("; ", errors)),
                HttpStatus.BAD_REQUEST
        );
    }

    // add constraint errors handler, etc

    @Data
    @AllArgsConstructor
    private static class ErrorResponse { // could be array of errors
        private String code;
        private String message;
    }
}