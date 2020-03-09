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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.Set;


@Entity
@Table(name = "appliance_type_states")
@Data  @NoArgsConstructor
public class TypeState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_id")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private ApplianceType type;

    @NonNull @Size(min = 2, max = 256, message = "Appliance type state key must be from {min} to {max} characters")
    @Column(name = "key", nullable = false, unique = true)
    private String key;

    @NonNull
    @ElementCollection
    @CollectionTable(
            name = "type_states_list",
            joinColumns=@JoinColumn(name = "type_state_id")
    )
    @Column(name = "values_list")
    private Set<String> values;

    public TypeState(
            @NonNull String key,
            @NonNull Set<String> values,
            @NonNull ApplianceType type
    ) {
        this.key = key;
        this.values = values;
        this.type = type;
    }
}
