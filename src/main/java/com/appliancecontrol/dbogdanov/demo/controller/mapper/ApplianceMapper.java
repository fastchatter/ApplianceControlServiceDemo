package com.appliancecontrol.dbogdanov.demo.controller.mapper;

import com.appliancecontrol.dbogdanov.demo.controller.dto.ApplianceResponse;
import com.appliancecontrol.dbogdanov.demo.jpa_entity.Appliance;
import com.appliancecontrol.dbogdanov.demo.jpa_entity.Setting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ApplianceMapper {
    @Mapping(source = "appliance.type.id", target = "type_id")
    @Mapping(source = "appliance.type.key", target = "type_key")
    @Mapping(source = "appliance.settings", target = "settings", qualifiedByName = "settingsObjectsMapToFlatMap")
    ApplianceResponse applianceToResponse(Appliance appliance);

    @Named("settingsObjectsMapToFlatMap")
    static Map<String, String> settingsObjectsMapToFlatMap(Map<String, Setting> settingMap) {
        return settingMap.values().stream()
                .collect(Collectors.toMap(Setting::getKey, Setting::getValue));
    }
}
