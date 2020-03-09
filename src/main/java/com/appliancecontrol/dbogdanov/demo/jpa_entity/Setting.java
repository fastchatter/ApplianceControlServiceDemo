package com.appliancecontrol.dbogdanov.demo.jpa_entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

@Entity
@Table(
        name = "appliance_settings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"appliance_id", "key", "value"})
)
@Data @NoArgsConstructor
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appliance_id")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Appliance appliance;

    @NonNull @Size(min = 2, max = 32, message = "Appliance setting key must be from {min} to {max} characters")
    @Column(name = "key", nullable = false)
    private String key;

    @NonNull @Size(min = 1, max = 1024, message = "Appliance setting value must be from {min} to {max} characters")
    @Column(name = "value", nullable = false)
    private String value;

    public Setting(@NonNull String key, @NonNull String value) {
        this.key = key;
        this.value = value;
    }
}
