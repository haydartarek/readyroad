package com.readyroad.readyroadbackend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class LoginRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesEmailIntoUsernameIdentifier() throws Exception {
        LoginRequest request = objectMapper.readValue(
                """
                        {
                          "email": "learner@example.com",
                          "password": "Secret123!"
                        }
                        """,
                LoginRequest.class);

        assertThat(request.getUsername()).isEqualTo("learner@example.com");
        assertThat(request.getPassword()).isEqualTo("Secret123!");
    }
}