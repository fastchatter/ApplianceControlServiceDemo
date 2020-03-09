package com.appliancecontrol.dbogdanov.demo.controller.dto;

import lombok.Data;

import java.util.Set;


@Data
public class TypeSettingResponse {
    private String key;
    private String name;
    private String defaultValue;
    private Set<String> values;
    private String unit;
}
