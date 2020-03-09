package com.appliancecontrol.dbogdanov.demo.repository;

import com.appliancecontrol.dbogdanov.demo.jpa_entity.ApplianceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplianceTypeRepository extends JpaRepository<ApplianceType, Long> {
}
