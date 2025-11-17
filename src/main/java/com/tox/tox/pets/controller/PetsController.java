package com.tox.tox.pets.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tox.tox.pets.model.*;
import com.tox.tox.pets.model.dto.*;
import com.tox.tox.pets.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 宠物信息前端控制器
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@RestController
@RequestMapping("/api")
public class PetsController {

    @Autowired
    private IPetsService petsService;

    @Autowired
    private IDictItemsService dictItemsService;

    @Autowired
    private IWeightLogService weightLogService;

    @Autowired
    private IHealthEventsService healthEventsService;

    @Autowired
    private IPetGalleryService petGalleryService;

    /**
     * 添加宠物
     */
    @PostMapping("/pets")
    @Transactional
    public ResponseEntity<String> addPet(@RequestBody PetRequestDTO petRequest) {
        // 设置创建时间
        petRequest.setCreatedAt(OffsetDateTime.now());
        boolean saved = petsService.save(petRequest);
        if (saved) {
            // 如果有头像URL，则存入相册表
            if (petRequest.getProfileImageUrl() != null && !petRequest.getProfileImageUrl().isEmpty()) {
                PetGallery gallery = new PetGallery();
                gallery.setPetId(petRequest.getId());
                gallery.setImageUrl(petRequest.getProfileImageUrl());
                gallery.setPublicId(petRequest.getProfileImagePublicId());
                gallery.setDescription("Pet Avatar");
                gallery.setCreatedAt(OffsetDateTime.now());
                petGalleryService.save(gallery);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("宠物添加成功，ID：" + petRequest.getId());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("宠物添加失败");
        }
    }

    /**
     * 获取宠物列表
     */
    @GetMapping("/pets")
    public ResponseEntity<List<Pets>> listPets() {
        List<Pets> pets = petsService.list();
        return ResponseEntity.ok(pets);
    }


    /**
     * (❗ 升级) GET /api/pets/page
     * (MyBatis-Plus Version)
     * (完全匹配你的 API 文档)
     */
    @GetMapping("/pets/page")
    public ResponseEntity<IPage<PetPageDTO>> getPetPage(
            // (❗) 接收 Query 参数, 匹配你的 API 文档
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        IPage<PetPageDTO> page = petsService.findPetsWithLikes(pageNum, pageSize);
        //根据likeCount排序
        page.setRecords(page.getRecords().stream()
                .sorted((p1, p2) -> Long.compare(p2.getLikeCount(), p1.getLikeCount()))
                .collect(Collectors.toList()));
        // (❗) IPage<...> 序列化后的 JSON 结构
        // (完美匹配你 API 文档里的 "records", "total", "size", "current")
        return ResponseEntity.ok(page);
    }

    /**
     * (❗ 新增) GET /api/pets/leaderboard
     * 获取点赞排行榜
     */
    @GetMapping("/pets/leaderboard")
    public ResponseEntity<List<PetLeaderboardDTO>> getLeaderboard(
            @RequestParam(defaultValue = "10") int topN
    ) {
        List<PetLeaderboardDTO> leaderboard = petsService.getLeaderboard(topN);
        return ResponseEntity.ok(leaderboard);
    }

    /**
     * 根据ID获取宠物信息
     */
    @GetMapping("/pets/{id}")
    public ResponseEntity<Pets> getPetById(@PathVariable Long id) {
        Pets pet = petsService.getById(id);
        if (pet != null) {
            return ResponseEntity.ok(pet);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

        /**

         * 根据ID获取宠物详细信息

         */

        @GetMapping("/pets/detail/{id}")

        public ResponseEntity<PetDetailDTO> getPetDetailById(@PathVariable Long id) {

            PetDetailDTO detailDTO = petsService.getPetDetailById(id);

            if (detailDTO == null) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            }

            return ResponseEntity.ok(detailDTO);

        }

    

        /**

         * 根据ID更新宠物信息

         */

                @PutMapping("/pets/{id}")

                @Transactional

                public ResponseEntity<Pets> updatePet(@PathVariable Long id, @RequestBody PetRequestDTO petRequest) {

                    // 确保ID一致

                    petRequest.setId(id);

                    // 不更新创建时间

                    Pets existingPet = petsService.getById(id);

                    if (existingPet != null) {

                        petRequest.setCreatedAt(existingPet.getCreatedAt());

            

                        boolean updated = petsService.updateById(petRequest);

                        if (updated) {

                            // 检查是否提供了新的、与之前不同的头像URL

                            String newProfileImageUrl = petRequest.getProfileImageUrl();

                            String oldProfileImageUrl = existingPet.getProfileImageUrl();

                            boolean isNewUrlProvided = newProfileImageUrl != null && !newProfileImageUrl.isEmpty() && !newProfileImageUrl.equals(oldProfileImageUrl);

            

                                            if (isNewUrlProvided) {

            

                                                PetGallery gallery = new PetGallery();

            

                                                gallery.setPetId(id);

            

                                                gallery.setImageUrl(newProfileImageUrl);

            

                                                gallery.setPublicId(petRequest.getProfileImagePublicId());

            

                                                gallery.setDescription("Pet Avatar");

            

                                                gallery.setCreatedAt(OffsetDateTime.now());

            

                                                petGalleryService.save(gallery);

            

                                            }

                            return ResponseEntity.ok(petRequest);

                        } else {

                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

                        }

                    } else {

                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

                    }

                }

    

        /**

         * 根据ID删除宠物

         */

        @DeleteMapping("/pets/{id}")

        public ResponseEntity<Void> deletePet(@PathVariable Long id) {

            boolean deleted = petsService.removeById(id);

            if (deleted) {

                return ResponseEntity.noContent().build();

            } else {

                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            }

        }

    

        /**

         * 根据物种查询宠物

         */

        @GetMapping("/pets/species/{species}")

        public ResponseEntity<List<Pets>> getPetsBySpecies(@PathVariable String species) {

            List<Pets> pets = petsService.getPetsBySpecies(species);

            return ResponseEntity.ok(pets);

        }

    }

    