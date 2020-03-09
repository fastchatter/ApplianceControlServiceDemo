package com.appliancecontrol.dbogdanov.demo.controller;

import com.appliancecontrol.dbogdanov.demo.controller.advice.ExceptionHandlerAdvice;
import com.appliancecontrol.dbogdanov.demo.controller.mapper.ApplianceMapper;
import com.appliancecontrol.dbogdanov.demo.controller.mapper.TypeSettingMapper;
import com.appliancecontrol.dbogdanov.demo.jpa_entity.Appliance;
import com.appliancecontrol.dbogdanov.demo.jpa_entity.ApplianceType;
import com.appliancecontrol.dbogdanov.demo.jpa_entity.Setting;
import com.appliancecontrol.dbogdanov.demo.jpa_entity.TypeSetting;
import com.appliancecontrol.dbogdanov.demo.jpa_entity.TypeState;
import com.appliancecontrol.dbogdanov.demo.service.ApplianceService;
import com.appliancecontrol.dbogdanov.demo.service.exceptions.ApplianceNotFoundException;
import com.appliancecontrol.dbogdanov.demo.service.exceptions.NoSuchSettingValueAvailableException;
import com.appliancecontrol.dbogdanov.demo.service.exceptions.NoSuchStateValueAvailableException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class ApplianceControllerTest {
    @Mock
    private ApplianceService applianceService;
    private ApplianceMapper applianceMapper = Mappers.getMapper(ApplianceMapper.class);
    private TypeSettingMapper typeSettingMapper = Mappers.getMapper(TypeSettingMapper.class);

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ApplianceController(applianceService, applianceMapper, typeSettingMapper))
                .setControllerAdvice(new ExceptionHandlerAdvice())
                .build();
    }

    @Test
    void testGetAppliance() throws Exception {
        var appliance = new Appliance();
        appliance.setName("robot");
        appliance.setState("app-state");

        var applianceType = new ApplianceType();
        applianceType.setId(22L);
        applianceType.setKey("type-key");
        appliance.setType(applianceType);

        var setting = new TypeSetting();
        setting.setKey("key1");
        setting.setValues(Set.of("0.2", "0.5"));
        applianceType.setSettings(Set.of(setting));

        applianceType.setState(new TypeState(
                "state", Set.of("on", "off"), applianceType
        ));

        appliance.addSetting(new Setting("key1", "1"));
        appliance.addSetting(new Setting("key2", "3"));

        when(applianceService.getAppliance(1L))
                .thenReturn(appliance);

        mockMvc.perform(get("/api/appliances/1")        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("robot"))
                .andExpect(jsonPath("$.type_key").value("type-key"))
                .andExpect(jsonPath("$.type_id").value(22))
                .andExpect(jsonPath("$.state").value("app-state"))
                .andExpect(jsonPath("$.settings").isMap())
                .andExpect(jsonPath("$.settings", hasKey("key1")))
                .andExpect(jsonPath("$.settings", hasValue("1")));
    }

    @Test
    void testApplianceNotFound() throws Exception {
        when(applianceService.getAppliance(2L))
                .thenThrow(new ApplianceNotFoundException("no appliance found whatever"));

        mockMvc.perform(get("/api/appliances/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("no-appliance-found"))
                .andExpect(jsonPath("$.message").value("No appliance found"));
    }

    @Test
    void testGetApplianceAvailableSettings() throws Exception {
        var setting = new TypeSetting();
        setting.setKey("key1");
        setting.setName("type setting name");
        setting.setUnit("m");
        setting.setValues(Set.of("0.2", "0.5"));
        when(applianceService.getAvailableSettings(1L))
                .thenReturn(Set.of(setting));

        mockMvc.perform(get("/api/appliances/1/available_settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].key").value("key1"))
                .andExpect(jsonPath("$[0].name").value("type setting name"))
                .andExpect(jsonPath("$[0].unit").value("m"))
                .andExpect(jsonPath("$[0].values").isArray())
                .andExpect(jsonPath("$[0].values", containsInAnyOrder("0.2", "0.5")));
    }

    @Test
    void testUpdateApplianceSettingsNoSuchSettingValue() throws Exception {
        var newSettings = Map.of("key1", "no-such-value");
        doThrow(new NoSuchSettingValueAvailableException("whatever"))
                .when(applianceService)
                .updateApplianceSettings(1L, newSettings);

        mockMvc.perform(put("/api/appliances/1/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newSettings))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad-request-value"))
                .andExpect(jsonPath("$.message").value("whatever"));
    }

    @Test
    void testGetAvailableStates() throws Exception {
        when(applianceService.getAvailableStates(1L))
                .thenReturn(Set.of("on", "off"));

        mockMvc.perform(get("/api/appliances/1/available_states"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$", containsInAnyOrder("on", "off")));
    }

    @Test
    void testUpdateApplianceStateNoSuchState() throws Exception {
        var incorrectState = "no-such-state";
        var newState = Map.of("state", incorrectState);
        doThrow(new NoSuchStateValueAvailableException("whatever"))
                .when(applianceService)
                .updateApplianceState(1L, incorrectState);

        mockMvc.perform(put("/api/appliances/1/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newState))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad-request-value"))
                .andExpect(jsonPath("$.message").value("whatever"));
    }

    @Test
    void testUpdateApplianceStateCantBeNull() throws Exception {
        mockMvc.perform(put("/api/appliances/1/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(Map.of()))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad-request-value"))
                .andExpect(jsonPath("$.message").value("state: must not be null"));
    }
}