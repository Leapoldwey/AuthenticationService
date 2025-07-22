package org.example.authenticationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.authenticationservice.dto.UserDto;
import org.example.authenticationservice.entity.Role;
import org.example.authenticationservice.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13-alpine")
            .withDatabaseName("authenticationservice")
            .withUsername("admin")
            .withPassword("admin");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void createUser_createWithRoleIsAdmin_newUser() throws Exception {
        UserDto userDto = new UserDto(
                UUID.randomUUID(),
                "mihailG",
                "password",
                "mihailG@mail.ru",
                Set.of("GUEST")
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getAccessToken("mihail@mail.ru"))
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void create_createWithRoleIsGuest_Unauthorized() throws Exception {
        UserDto userDto = new UserDto(
                UUID.randomUUID(),
                "mihailG",
                "password",
                "mihailG@mail.ru",
                Set.of("GUEST")
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getAccessToken("alex@mail.ru"))
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void findAllUsers_withAuthentication_allUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .header("Authorization", "Bearer " + getAccessToken("mihail@mail.ru")))
                .andExpect(status().isOk());
    }

    @Test
    public void findAllUsers_withoutAuthentication_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isUnauthorized());
    }

    private String getAccessToken(String email) throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/users/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "password"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value("%s".formatted(email)))
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("refreshToken").exists());

        String response = result.andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("accessToken").asText();
    }
}