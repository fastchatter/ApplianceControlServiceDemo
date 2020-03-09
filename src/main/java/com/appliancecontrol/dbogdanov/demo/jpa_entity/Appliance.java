package com.appliancecontrol.dbogdanov.demo.jpa_entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Entity
@Table(name = "appliances")
@Data @NoArgsConstructor
public class Appliance {
    public Appliance(String name, ApplianceType type, String state) {
        this.name = name;
        this.type = type;
        this.state = state;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull @Size(min = 2, max = 64, message = "Appliance name must be from {min} to {max} characters")
    @Column(name = "name", nullable = false)
    private String name;

    @NonNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private ApplianceType type;

    @NonNull @Size(min = 2, max = 32, message = "State must be from {min} to {max} characters")
    @Column(name = "state")
    private String state;

    @OneToMany(mappedBy = "appliance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @MapKey(name = "key")
    private Map<String, Setting> settings = new HashMap<>();

    public void addSetting(@NonNull Setting setting) {
        settings.put(setting.getKey(), setting);
        setting.setAppliance(this);
    }

    public Setting getSetting(@NonNull String key) {
        return settings.get(key);
    }

    public void updateSetting(@NonNull String key, @NonNull String value) {
        Setting setting = settings.get(key);
        setting.setValue(value);
    }

    public Set<TypeSetting> getAvailableSettings() {
        return getType()  //
                .getSettings();
    }
}
