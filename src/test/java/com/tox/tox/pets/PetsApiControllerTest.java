package com.tox.tox.pets;

import com.tox.tox.pets.model.Pets;
import com.tox.tox.pets.service.IPetsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PetsApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IPetsService petsService;

    // ========== 宠物接口测试 ==========

    @Test
    void testGetPetList() throws Exception {
        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetPetPage() throws Exception {
        mockMvc.perform(get("/api/pets/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records").isArray())
                .andExpect(jsonPath("$.total").isNumber());
    }

    @Test
    void testGetPetLeaderboard() throws Exception {
        mockMvc.perform(get("/api/pets/leaderboard")
                        .param("topN", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetPetByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/pets/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPetDetailNotFound() throws Exception {
        mockMvc.perform(get("/api/pets/detail/99999"))
                .andExpect(status().isNotFound());
    }

    // ========== 点赞接口测试 ==========

    @Test
    void testLikePet() throws Exception {
        // 先创建一个宠物
        Pets pet = new Pets();
        pet.setName("接口测试宠物_" + System.currentTimeMillis());
        pet.setSpeciesId(1L);
        pet.setCreatedAt(OffsetDateTime.now());
        petsService.save(pet);

        Long petId = pet.getId();

        // 点赞
        mockMvc.perform(post("/api/pets/" + petId + "/like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 获取点赞数
        mockMvc.perform(get("/api/pets/" + petId + "/likes/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.petId").value(petId))
                .andExpect(jsonPath("$.count").isNumber());
    }

    @Test
    void testLikeNonExistentPet() throws Exception {
        mockMvc.perform(post("/api/pets/99999/like"))
                .andExpect(status().isBadRequest());
    }

    // ========== 认证接口测试 ==========

    @Test
    void testRegister() throws Exception {
        String username = "apitest_" + System.currentTimeMillis();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"test123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.userId").isNumber());
    }

    @Test
    void testLogin() throws Exception {
        String username = "logintest_" + System.currentTimeMillis();
        String password = "password123";

        // 先注册
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}}"));

        // 再登录
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tokenName").exists());
    }

    @Test
    void testLoginInvalidCredentials() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nonexistent\",\"password\":\"wrong\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetUserInfoWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/auth/info"))
                .andExpect(status().isUnauthorized());
    }
}
