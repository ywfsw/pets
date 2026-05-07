package com.tox.tox.pets.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class NotificationPrefsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean healthEnabled;
    private Boolean medicationEnabled;
    private Boolean feedingEnabled;
    private Boolean bathingEnabled;
    private Boolean birthdayEnabled;
    private Integer healthDaysBefore;
    private Integer birthdayDaysBefore;

    public NotificationPrefsDTO() {}

    public NotificationPrefsDTO(Boolean healthEnabled, Boolean medicationEnabled,
                                Boolean feedingEnabled, Boolean bathingEnabled,
                                Boolean birthdayEnabled, Integer healthDaysBefore,
                                Integer birthdayDaysBefore) {
        this.healthEnabled = healthEnabled;
        this.medicationEnabled = medicationEnabled;
        this.feedingEnabled = feedingEnabled;
        this.bathingEnabled = bathingEnabled;
        this.birthdayEnabled = birthdayEnabled;
        this.healthDaysBefore = healthDaysBefore;
        this.birthdayDaysBefore = birthdayDaysBefore;
    }
}
