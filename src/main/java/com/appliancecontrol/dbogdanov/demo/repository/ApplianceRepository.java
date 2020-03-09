package com.appliancecontrol.dbogdanov.demo.repository;

import com.appliancecontrol.dbogdanov.demo.jpa_entity.Appliance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ApplianceRepository extends JpaRepository<Appliance, Long> {
}
