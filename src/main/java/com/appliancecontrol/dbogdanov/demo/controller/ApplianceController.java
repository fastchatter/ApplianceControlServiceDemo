package com.appliancecontrol.dbogdanov.demo.controller;

import com.appliancecontrol.dbogdanov.demo.controller.dto.ApplianceResponse;
import com.appliancecontrol.dbogdanov.demo.controller.dto.StateRequest;
import com.appliancecontrol.dbogdanov.demo.controller.dto.TypeSettingResponse;
import com.appliancecontrol.dbogdanov.demo.controller.mapper.ApplianceMapper;
import com.appliancecontrol.dbogdanov.demo.controller.mapper.TypeSettingMapper;
import com.appliancecontrol.dbogdanov.demo.jpa_entity.Appliance;
import com.appliancecontrol.dbogdanov.demo.jpa_entity.TypeSetting;
import com.appliancecontrol.dbogdanov.demo.service.ApplianceService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Set;

import static java.util.stream.Collectors.toSet;


@RestController
public class ApplianceController {
    private ApplianceService applianceService;
    private ApplianceMapper applianceMapper;
    private TypeSettingMapper typeSettingMapper;

    public ApplianceController(ApplianceService applianceService, ApplianceMapper applianceMapper, TypeSettingMapper typeSettingMapper) {
        this.applianceService = applianceService;
        this.applianceMapper = applianceMapper;
        this.typeSettingMapper = typeSettingMapper;
    }

    // @GetMapping(path = "/api/appliances/", produces = MediaType.APPLICATION_JSON_VALUE)

    @GetMapping(path = "/api/appliances/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApplianceResponse getAppliance(@PathVariable Long id) {
        Appliance appliance = applianceService.getAppliance(id);
        return applianceMapper.applianceToResponse(appliance);
    }

    @GetMapping(path = "/api/appliances/{id}/available_settings", produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<TypeSettingResponse> getApplianceAvailableSettings(@PathVariable Long id) {
        Set<TypeSetting> availableSettings = applianceService.getAvailableSettings(id);
        return availableSettings.stream()
                .map(typeSettingMapper::typeSettingToResponse)
                .collect(toSet());
    }

    @GetMapping(path = "/api/appliances/{id}/available_states", produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<String> getApplianceAvailableStates(@PathVariable Long id) {
        return applianceService.getAvailableStates(id);
    }

    @PutMapping(path = "/api/appliances/{id}/settings", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateApplianceSettings(
            @PathVariable Long id,
            @RequestBody HashMap<String, String> settings
    ) {
        applianceService.updateApplianceSettings(id, settings);
    }

    @PutMapping(path = "/api/appliances/{id}/state", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateApplianceState(
            @PathVariable Long id,
            @Valid @RequestBody StateRequest stateRequest
    ) {
            applianceService.updateApplianceState(id, stateRequest.getState());
    }
}
