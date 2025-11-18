package com.tox.tox.pets.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetLeaderboardDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int rank;
    private Long petId;
    private String name;
    private String profileImageUrl; // 新增字段
    private long likeCount;

}
