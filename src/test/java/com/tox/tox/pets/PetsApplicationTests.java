package com.tox.tox.pets;

import com.tox.tox.pets.model.Pets;
import com.tox.tox.pets.model.Users;
import com.tox.tox.pets.service.IPetsService;
import com.tox.tox.pets.service.IUsersService;
import com.tox.tox.pets.service.LikingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PetsApplicationTests {

    @Autowired
    private IPetsService petsService;

    @Autowired
    private IUsersService usersService;

    @Autowired
    private LikingService likingService;

    @Test
    void contextLoads() {
        assertNotNull(petsService);
        assertNotNull(usersService);
        assertNotNull(likingService);
    }

    // ========== 用户服务测试 ==========

    @Test
    void testUserRegister() {
        String username = "testuser_" + System.currentTimeMillis();
        String password = "test123";

        Users user = usersService.register(username, password);

        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertNotNull(user.getPassword());
    }

    @Test
    void testUserLogin() {
        String username = "logintest_" + System.currentTimeMillis();
        String password = "password123";

        usersService.register(username, password);
        String token = usersService.login(username, password);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testUserLoginInvalidPassword() {
        String username = "authtest_" + System.currentTimeMillis();
        String password = "correctpassword";
        String wrongPassword = "wrongpassword";

        usersService.register(username, password);

        assertThrows(Exception.class, () -> {
            usersService.login(username, wrongPassword);
        });
    }

    // ========== 宠物服务测试 ==========

    @Test
    void testCreatePet() {
        Pets pet = new Pets();
        pet.setName("测试宠物");
        pet.setSpeciesId(1L);
        pet.setBreedId(1L);
        pet.setBirthday(OffsetDateTime.now().toLocalDate());
        pet.setCreatedAt(OffsetDateTime.now());

        boolean result = petsService.save(pet);

        assertTrue(result);
        assertNotNull(pet.getId());
    }

    @Test
    void testGetPetById() {
        Pets pet = new Pets();
        pet.setName("查询测试宠物");
        pet.setSpeciesId(1L);
        pet.setCreatedAt(OffsetDateTime.now());
        petsService.save(pet);

        Pets foundPet = petsService.getById(pet.getId());

        assertNotNull(foundPet);
        assertEquals("查询测试宠物", foundPet.getName());
    }

    @Test
    void testUpdatePet() {
        Pets pet = new Pets();
        pet.setName("原始名字");
        pet.setSpeciesId(1L);
        pet.setCreatedAt(OffsetDateTime.now());
        petsService.save(pet);

        pet.setName("新名字");
        boolean result = petsService.updateById(pet);

        assertTrue(result);

        Pets updatedPet = petsService.getById(pet.getId());
        assertEquals("新名字", updatedPet.getName());
    }

    @Test
    void testDeletePet() {
        Pets pet = new Pets();
        pet.setName("待删除宠物");
        pet.setSpeciesId(1L);
        pet.setCreatedAt(OffsetDateTime.now());
        petsService.save(pet);

        Long petId = pet.getId();
        boolean result = petsService.removeById(petId);

        assertTrue(result);
        assertNull(petsService.getById(petId));
    }

    @Test
    void testGetPetsBySpecies() {
        Pets pet1 = new Pets();
        pet1.setName("狗狗1");
        pet1.setSpeciesId(1L);
        pet1.setCreatedAt(OffsetDateTime.now());
        petsService.save(pet1);

        Pets pet2 = new Pets();
        pet2.setName("狗狗2");
        pet2.setSpeciesId(1L);
        pet2.setCreatedAt(OffsetDateTime.now());
        petsService.save(pet2);

        List<Pets> dogs = petsService.getPetsBySpecies("dog");

        assertNotNull(dogs);
        assertTrue(dogs.size() >= 2);
    }

    // ========== 点赞服务测试 ==========

    @Test
    void testLikePet() {
        Pets pet = new Pets();
        pet.setName("点赞测试宠物");
        pet.setSpeciesId(1L);
        pet.setCreatedAt(OffsetDateTime.now());
        petsService.save(pet);

        Long petId = pet.getId();

        // 点赞
        likingService.likePet(petId);

        // 获取点赞数
        long count = likingService.getPetLikeCount(petId);

        assertTrue(count >= 1);
    }

    @Test
    void testGetLikeCount() {
        Pets pet = new Pets();
        pet.setName("计数测试宠物");
        pet.setSpeciesId(1L);
        pet.setCreatedAt(OffsetDateTime.now());
        petsService.save(pet);

        Long petId = pet.getId();

        // 多次点赞
        likingService.likePet(petId);
        likingService.likePet(petId);

        long count = likingService.getPetLikeCount(petId);

        assertTrue(count >= 2);
    }

    @Test
    void testLikeNonExistentPet() {
        assertThrows(IllegalArgumentException.class, () -> {
            likingService.likePet(99999L);
        });
    }
}
