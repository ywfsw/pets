package com.tox.tox.pets.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class NotificationItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;
    private Long petId;
    private String petName;
    private String message;
    private String detail;
    private Integer urgency;
    private String sourceId;
    private String pageTarget;

    public NotificationItemDTO() {}

    public NotificationItemDTO(String type, Long petId, String petName,
                               String message, String detail, Integer urgency) {
        this.type = type;
        this.petId = petId;
        this.petName = petName;
        this.message = message;
        this.detail = detail;
        this.urgency = urgency;
    }
}
