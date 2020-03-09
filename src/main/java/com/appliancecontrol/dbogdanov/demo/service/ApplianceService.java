package com.appliancecontrol.dbogdanov.demo.service;

import com.appliancecontrol.dbogdanov.demo.jpa_entity.Appliance;
import com.appliancecontrol.dbogdanov.demo.jpa_entity.TypeSetting;
import com.appliancecontrol.dbogdanov.demo.repository.ApplianceRepository;
import com.appliancecontrol.dbogdanov.demo.service.exceptions.ApplianceNotFoundException;
import com.appliancecontrol.dbogdanov.demo.service.exceptions.NoSuchSettingAvailableException;
import com.appliancecontrol.dbogdanov.demo.service.exceptions.NoSuchSettingValueAvailableException;
import com.appliancecontrol.dbogdanov.demo.service.exceptions.NoSuchStateValueAvailableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

@Service
public class ApplianceService {
    private ApplianceRepository applianceRepository;

    public ApplianceService(ApplianceRepository applianceRepository) {
        this.applianceRepository = applianceRepository;
    }

    // actually DTO is needed here, but skipped for simplicity
    @Transactional(readOnly = true)
    public Appliance getAppliance(Long id) {
        return applianceRepository.findById(id)
                .orElseThrow(() -> new ApplianceNotFoundException(
                        String.format("Appliance with id '%s' not found", id)
                ));
    }

    @Transactional(readOnly = true)
    public Set<String> getAvailableStates(Long applianceId) {
        return getAppliance(applianceId)
                .getType()  // such chain is not cool, but lets consider that its safe to do so
                .getState()
                .getValues();
    }


    @Transactional(readOnly = true)
    public Set<TypeSetting> getAvailableSettings(Long applianceId) {
        return getAppliance(applianceId)
                .getAvailableSettings();
    }

    @Transactional
    public void updateApplianceState(Long applianceId, String state) {
        Appliance appliance = getAppliance(applianceId);
        Set<String> availableStates = getAvailableStates(applianceId);
        if (!availableStates.contains(state)) {
            throw new NoSuchStateValueAvailableException(
                    String.format("Incorrect state '%s' for appliance type", state)
            );
        }
        appliance.setState(state);
    }

    @Transactional
    public void updateApplianceSettings(Long applianceId, Map<String, String> settings) {
        Appliance appliance = getAppliance(applianceId);

        // actually this piece should belong to domain class
        // but domain classes are skipped for simplicity
        Map<String, Set<String>> availableSettings = appliance.getAvailableSettings().stream()
                .collect(toMap(TypeSetting::getKey, TypeSetting::getValues));

        for (var entry : settings.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (!availableSettings.containsKey(key)) {
                throw new NoSuchSettingAvailableException(
                        String.format("No setting with key '%s' available!", key)
                );
            }

            Set<String> availableSettingValues = availableSettings.get(key);
            if (!availableSettingValues.contains(value)) {
                throw new NoSuchSettingValueAvailableException(
                        String.format("Value '%s' is invalid for setting '%s'", value, key)
                );
            }

            // NOTE: lets pretend that we are sending signal/event here instead of just saving setting to db
            appliance.updateSetting(key, value);
        }
    }
}
