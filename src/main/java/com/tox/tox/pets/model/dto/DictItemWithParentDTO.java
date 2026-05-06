package com.tox.tox.pets.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
public class DictItemWithParentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String dictCode;
    private String itemValue;
    private String itemLabel;
    private Long parentId;
    private String parentLabel;
    private Integer sortOrder;
    private String notes;
    private OffsetDateTime createdAt;
}
