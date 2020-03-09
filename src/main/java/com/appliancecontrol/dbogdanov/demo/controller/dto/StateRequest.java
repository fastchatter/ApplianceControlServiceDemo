package com.appliancecontrol.dbogdanov.demo.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class StateRequest {
    @NotNull
    @Size(min = 2, max = 32, message = "Appliance state must be from {min} to {max} characters")
    private String state;
}
