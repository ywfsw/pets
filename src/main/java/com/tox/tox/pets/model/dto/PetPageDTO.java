// (在 com.example.pets.dto 包中)
package com.tox.tox.pets.model.dto;

import com.tox.tox.pets.model.Pets;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 宠物分页列表的 DTO, 包含点赞数
 */
@Data
@NoArgsConstructor
public class PetPageDTO {

    // --- Pet 基础信息 ---
    private Long id;
    private String name;
    private Long speciesId;
    private Long breedId;
    private LocalDate birthday;
    private long likeCount;
    private String profileImageUrl;
    private String profileImagePublicId;

    /**
     * (推荐) 转换构造函数
     * @param pet 数据库 Pet 实体
     */
    public PetPageDTO(Pets pet) {
        this.id = pet.getId();
        this.name = pet.getName();
        this.speciesId = pet.getSpeciesId();
        this.breedId = pet.getBreedId();
        this.birthday = pet.getBirthday();
        this.profileImageUrl = pet.getProfileImageUrl();
        this.profileImagePublicId = pet.getProfileImagePublicId();
    }
}