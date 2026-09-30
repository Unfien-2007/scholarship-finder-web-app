package com.scholarshipfinder.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import com.scholarshipfinder.model.EducationLevel;

public final class AuthDtos {
    private AuthDtos() { }

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) { }
    public record SignupRequest(
            @NotBlank String fullName,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, max = 72) String password,
            @NotNull @DecimalMin("0.0") @DecimalMax("100.0") Double gwa,
            @NotBlank String location,
            @NotBlank String incomeBracket,
            @NotNull EducationLevel educationLevel) { }
    public record LoginResponse(String token, String role, String fullName) { }
}
