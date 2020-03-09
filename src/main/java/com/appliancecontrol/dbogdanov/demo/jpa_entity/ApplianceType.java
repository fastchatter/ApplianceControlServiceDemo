package com.appliancecontrol.dbogdanov.demo.jpa_entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "appliance_types")
@Data @NoArgsConstructor
public class ApplianceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull @Size(min = 2, max = 256, message = "Appliance type key must be from {min} to {max} characters")
    @Column(name = "key", unique = true, nullable = false)
    private String key;

    @NonNull @Size(min = 2, max = 128, message = "Appliance type name must be from {min} to {max} characters")
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    private Set<TypeSetting> settings = new HashSet<>();

    @OneToOne(mappedBy = "type", cascade = CascadeType.ALL, optional = false)
    @NonNull
    private TypeState state;

    public ApplianceType(@NonNull String key, @NonNull String name) {
        this.key = key;
        this.name = name;
    }

    public void addSetting(@NonNull TypeSetting setting) {
        settings.add(setting);
        setting.setType(this);
    }
}
