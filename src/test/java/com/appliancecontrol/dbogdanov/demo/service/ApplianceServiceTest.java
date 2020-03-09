package com.appliancecontrol.dbogdanov.demo.service;

import com.appliancecontrol.dbogdanov.demo.jpa_entity.*;
import com.appliancecontrol.dbogdanov.demo.repository.ApplianceRepository;
import com.appliancecontrol.dbogdanov.demo.service.exceptions.ApplianceNotFoundException;
import com.appliancecontrol.dbogdanov.demo.service.exceptions.NoSuchSettingAvailableException;
import com.appliancecontrol.dbogdanov.demo.service.exceptions.NoSuchSettingValueAvailableException;
import com.appliancecontrol.dbogdanov.demo.service.exceptions.NoSuchStateValueAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ApplianceServiceTest {
    private ApplianceService applianceService;

    @Mock
    private ApplianceRepository applianceRepository;

    @BeforeEach
    public void init() {
        applianceService = new ApplianceService(applianceRepository);
    }

    @Test
    void testGetAppliance() {
        var appliance = new Appliance();
        when(applianceRepository.findById(2L))
                .thenReturn(Optional.of(appliance));

        assertThat(applianceService.getAppliance(2L)).isSameAs(appliance);
    }

    @Test
    void whenApplianceNotFound() {
        when(applianceRepository.findById(1L))
                .thenReturn(Optional.empty());

        var ex = assertThrows(
                ApplianceNotFoundException.class,
                () -> applianceService.getAppliance(1L)
        );

        assertThat(ex.getMessage()).isEqualTo("Appliance with id '1' not found");
    }

    private Appliance getApplianceWithTypeStates(Set<String> states) {
        var applianceType = new ApplianceType();
        applianceType.setState(new TypeState(
                "state", states, applianceType
        ));
        var appliance = new Appliance();
        appliance.setType(applianceType);
        return appliance;
    }

    @Test
    void testGetAvailableStates() {
        var expectedStates = Set.of("on", "off");
        Appliance appliance = getApplianceWithTypeStates(expectedStates);

        when(applianceRepository.findById(34L))
                .thenReturn(Optional.of(appliance));

        assertThat(applianceService.getAvailableStates(34L))
                .isEqualTo(expectedStates);
    }

    @Test
    void testUpdateApplianceState() {
        var expectedStates = Set.of("on", "off");
        Appliance appliance = getApplianceWithTypeStates(expectedStates);
        appliance.setState("on");

        when(applianceRepository.findById(45L))
                .thenReturn(Optional.of(appliance));

        applianceService.updateApplianceState(45L, "off");
        assertThat(appliance.getState())
                .isEqualTo("off");
    }

    @Test
    void testUpdateApplianceStateException() {
        var expectedStates = Set.of("on", "off");
        Appliance appliance = getApplianceWithTypeStates(expectedStates);
        appliance.setState("on");

        when(applianceRepository.findById(45L))
                .thenReturn(Optional.of(appliance));

        var ex = assertThrows(
                NoSuchStateValueAvailableException.class,
                () -> applianceService.updateApplianceState(45L, "non-existing-state")
        );
        assertThat(ex.getMessage())
                .isEqualTo("Incorrect state 'non-existing-state' for appliance type");
    }


    private Appliance getApplianceWithTypeSettings(Set<TypeSetting> settings) {
        var applianceType = new ApplianceType();
        applianceType.setSettings(settings);
        var appliance = new Appliance();
        appliance.setType(applianceType);
        return appliance;
    }

    private TypeSetting getTypeSettingWithValues(String key, Set<String> values) {
        var setting = new TypeSetting();
        setting.setKey(key);
        setting.setValues(values);
        return setting;
    }

    @Test
    void testGetAvailableSettings() {
        var expectedSettings = Set.of(
                getTypeSettingWithValues("key1", Set.of("0.2", "0.5")),
                getTypeSettingWithValues("key2", Set.of("300", "400"))
        );
        var appliance = getApplianceWithTypeSettings(expectedSettings);

        when(applianceRepository.findById(5L))
                .thenReturn(Optional.of(appliance));

        assertThat(applianceService.getAvailableSettings(5L))
                .isEqualTo(expectedSettings);
    }

    @Test
    void testUpdateApplianceSettings() {
        var expectedSettings = Set.of(
                getTypeSettingWithValues("key1", Set.of("1", "2")),
                getTypeSettingWithValues("key2", Set.of("3", "4"))
        );
        var appliance = getApplianceWithTypeSettings(expectedSettings);
        appliance.addSetting(new Setting("key1", "1"));
        appliance.addSetting(new Setting("key2", "3"));
        when(applianceRepository.findById(5L))
                .thenReturn(Optional.of(appliance));

        applianceService.updateApplianceSettings(5L, Map.of("key1", "2"));
        assertThat(appliance.getSetting("key1").getValue()).isEqualTo("2");
    }

    @Test
    void testNoSuchSettingAvailableException() {
        var expectedSettings = Set.of(
                getTypeSettingWithValues("key1", Set.of("1", "2")),
                getTypeSettingWithValues("key2", Set.of("3", "4"))
        );
        var appliance = getApplianceWithTypeSettings(expectedSettings);
        appliance.addSetting(new Setting("key1", "1"));
        appliance.addSetting(new Setting("key2", "3"));
        when(applianceRepository.findById(5L))
                .thenReturn(Optional.of(appliance));

        var ex = assertThrows(
                NoSuchSettingAvailableException.class,
                () -> applianceService.updateApplianceSettings(5L, Map.of("no-such-key", "2"))
        );
        assertThat(ex.getMessage()).isEqualTo("No setting with key 'no-such-key' available!");
    }

    @Test
    void testNoSuchSettingValueAvailableException() {
        var expectedSettings = Set.of(
                getTypeSettingWithValues("key1", Set.of("1", "2")),
                getTypeSettingWithValues("key2", Set.of("3", "4"))
        );
        var appliance = getApplianceWithTypeSettings(expectedSettings);
        appliance.addSetting(new Setting("key1", "1"));
        appliance.addSetting(new Setting("key2", "3"));
        when(applianceRepository.findById(5L))
                .thenReturn(Optional.of(appliance));

        var ex = assertThrows(
                NoSuchSettingValueAvailableException.class,
                () -> applianceService.updateApplianceSettings(5L, Map.of("key2", "no-such-value"))
        );
        assertThat(ex.getMessage()).isEqualTo("Value 'no-such-value' is invalid for setting 'key2'");
    }
}