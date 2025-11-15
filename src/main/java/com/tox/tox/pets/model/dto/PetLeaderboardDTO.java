package com.tox.tox.pets.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetLeaderboardDTO {

    private int rank;
    private Long petId;
    private String name;
    private long likeCount;

}
