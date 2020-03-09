package com.appliancecontrol.dbogdanov.demo.controller.mapper;

import com.appliancecontrol.dbogdanov.demo.controller.dto.TypeSettingResponse;
import com.appliancecontrol.dbogdanov.demo.jpa_entity.TypeSetting;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TypeSettingMapper {
    TypeSettingResponse typeSettingToResponse(TypeSetting setting);
}
