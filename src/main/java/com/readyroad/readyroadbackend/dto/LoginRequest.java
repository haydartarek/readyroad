package com.readyroad.readyroadbackend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Login Request DTO
 *
 * @author ReadyRoad Team
 * @since 2026-01-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @JsonAlias("email")
    @NotBlank(message = "Username or email is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
