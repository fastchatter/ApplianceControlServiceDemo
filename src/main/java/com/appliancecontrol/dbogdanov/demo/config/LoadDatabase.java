package com.appliancecontrol.dbogdanov.demo.config;


import com.appliancecontrol.dbogdanov.demo.jpa_entity.Appliance;
import com.appliancecontrol.dbogdanov.demo.jpa_entity.ApplianceType;
import com.appliancecontrol.dbogdanov.demo.jpa_entity.Setting;
import com.appliancecontrol.dbogdanov.demo.jpa_entity.TypeSetting;
import com.appliancecontrol.dbogdanov.demo.jpa_entity.TypeState;
import com.appliancecontrol.dbogdanov.demo.repository.ApplianceRepository;
import com.appliancecontrol.dbogdanov.demo.repository.ApplianceTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@Slf4j
public class LoadDatabase {
    @Bean
    public CommandLineRunner initDemoDatabase(
            ApplianceRepository applianceRepository,
            ApplianceTypeRepository applianceTypeRepository
    ) {
        return (args) -> {
            log.info("Preloading appliances");

            var vacuumType = new ApplianceType("robot-vacuum-sf3", "Automatic Vacuum Model SF3");
            vacuumType.addSetting(new TypeSetting(
                    "vacuum.speed", "speed of movement",
                    "0.2", Set.of("0.2", "0.5", "1.0"),
                    "m/s"
            ));
            vacuumType.addSetting(new TypeSetting(
                    "vacuum.power", "suction power of cleaning",
                    "300", Set.of("300", "400", "500"),
                    "aw"
            ));
            // builder pattern should be applied instead of bunch of .set* methods
            vacuumType.setState(new TypeState(
                    "vacuum.state", Set.of("on", "off", "charging", "cleaning", "stuck"), vacuumType
            ));

            vacuumType = applianceTypeRepository.save(vacuumType);

            var jacksVacuumCleaner = new Appliance(
                    "jack's vacuum",
                    vacuumType,
                    "off"
            );
            jacksVacuumCleaner.addSetting(new Setting("vacuum.speed", "0.2"));
            jacksVacuumCleaner.addSetting(new Setting("vacuum.power", "300"));
            applianceRepository.save(jacksVacuumCleaner);

            var bobsFavoriteVacuum = new Appliance(
                    "Bob's vacuum",
                    vacuumType,
                    "on"
            );
            bobsFavoriteVacuum.addSetting(new Setting("vacuum.speed", "1.0"));
            bobsFavoriteVacuum.addSetting(new Setting("vacuum.power", "500"));
            applianceRepository.save(bobsFavoriteVacuum);

            var coffeeMachineType = new ApplianceType(
                    "coffee-machine-home-sm5", "Simple Coffee Machine Model SM5"
            );
            coffeeMachineType.addSetting(new TypeSetting(
                    "coffee.beans", "amount of coffee beans to grind",
                    "10", Set.of("5", "10", "20"),
                    "g"
            ));
            coffeeMachineType.addSetting(new TypeSetting(
                    "coffee.milk", "amount of milk to add to coffee",
                    "50", Set.of("0", "100", "200"),
                    "ml"
            ));
            coffeeMachineType.setState(new TypeState(
                    "coffee.state", Set.of("on", "off", "brewing", "need-to-clean"), coffeeMachineType
            ));

            coffeeMachineType = applianceTypeRepository.save(coffeeMachineType);

            var jacksCoffeeMachine = new Appliance(
                    "jack's coffee machine",
                    coffeeMachineType,
                    "on"
            );
            jacksCoffeeMachine.addSetting(new Setting("coffee.beans", "20"));
            jacksCoffeeMachine.addSetting(new Setting("coffee.milk", "0"));
            applianceRepository.save(jacksCoffeeMachine);

        };
    }
}
