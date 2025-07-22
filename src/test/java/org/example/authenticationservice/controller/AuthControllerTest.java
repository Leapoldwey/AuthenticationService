package org.example.authenticationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.authenticationservice.dto.SignUpRequest;
import org.example.authenticationservice.entity.RefreshToken;
import org.example.authenticationservice.entity.User;
import org.example.authenticationservice.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

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
    void signUp_dataIsValid_newUser() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest(
                "mihailG@mail.ru",
                "password",
                "MihailG"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/users/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("email").value("mihailG@mail.ru"));
    }

    @Test
    void signIn_dataIsValid_authenticationAndGivenAccessAndRefreshToken() throws Exception {
        //given
        //liquibase init

        mockMvc.perform(MockMvcRequestBuilders.post("/users/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "mihail@mail.ru",
                                    "password": "password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value("mihail@mail.ru"))
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("refreshToken").exists());
    }

    @Test
    void refreshToken_sendsRefreshToken_newAccessToken() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/users/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "mihail@mail.ru",
                                  "password": "password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value("mihail@mail.ru"))
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("refreshToken").exists());

        String response = result.andReturn().getResponse().getContentAsString();
        String currentAccessToken = objectMapper.readTree(response).get("accessToken").asText();
        String refreshToken = objectMapper.readTree(response).get("refreshToken").asText();

        ResultActions refresh = mockMvc.perform(MockMvcRequestBuilders.post("/users/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "refreshToken": "%s"
                                }
                                """.formatted(refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value("mihail@mail.ru"))
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("refreshToken").exists());

        String refreshResponse = refresh.andReturn().getResponse().getContentAsString();
        String newAccessToken = objectMapper.readTree(refreshResponse).get("refreshToken").asText();

        assertNotNull(newAccessToken);
        assertNotEquals(currentAccessToken, newAccessToken);

    }

    @Test
    void logout_sendRefreshToken_TokenIsRevoked() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/users/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "kate@mail.ru",
                                  "password": "password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value("kate@mail.ru"))
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("refreshToken").exists());

        String response = result.andReturn().getResponse().getContentAsString();
        String refreshToken = objectMapper.readTree(response).get("refreshToken").asText();

        ResultActions logout = mockMvc.perform(MockMvcRequestBuilders.post("/users/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "refreshToken": "%s"
                                }
                                """.formatted(refreshToken)))
                .andExpect(status().isNoContent());

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken).orElse(null);
        assertNotNull(refreshToken);
        assertTrue(token.isRevoked());
    }
}