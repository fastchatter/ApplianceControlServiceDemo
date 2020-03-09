package com.appliancecontrol.dbogdanov.demo.controller.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ApplianceResponse {
    private Long id;
    private String name;
    private String type_id;
    private String type_key;
    private String state;
    private Map<String, String> settings;
}
