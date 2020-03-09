package com.appliancecontrol.dbogdanov.demo.jpa_entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.Set;


@Entity
@Table(name = "appliance_type_settings")
@Data  @NoArgsConstructor
public class TypeSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull @Size(min = 2, max = 32, message = "Appliance setting key must be from {min} to {max} characters")
    @Column(name = "key", nullable = false, unique = true)
    private String key;

    @NonNull @Size(min = 2, max = 128, message = "Appliance type setting name must be from {min} to {max} characters")
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_id")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @NonNull
    private ApplianceType type;

    @NonNull @Size(min = 1, max = 1024, message = "Appliance setting value must be from {min} to {max} characters")
    @Column(name = "default_value")
    private String defaultValue;

    @NonNull
    @ElementCollection
    @CollectionTable(
            name = "type_settings_list",
            joinColumns=@JoinColumn(name = "type_setting_id")
    )
    @Column(name = "values_list")
    private Set<String> values;

    @NonNull @Size(min = 1, max = 16, message = "Appliance setting value unit must be from {min} to {max} characters")
    @Column(name = "unit")
    private String unit;

    public TypeSetting(
            @NonNull String key, @NonNull String name,
            @NonNull String defaultValue, @NonNull Set<String> values,
            @NonNull String unit
    ) {
        this.key = key;
        this.name = name;
        this.defaultValue = defaultValue;
        this.values = values;
        this.unit = unit;
    }
}
