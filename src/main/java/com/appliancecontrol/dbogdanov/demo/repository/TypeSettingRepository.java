package com.appliancecontrol.dbogdanov.demo.repository;

import com.appliancecontrol.dbogdanov.demo.jpa_entity.TypeSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeSettingRepository extends JpaRepository<TypeSetting, Long> {
}
