package com.scholarshipfinder.dto;

import com.scholarshipfinder.model.EducationLevel;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProfileUpdateRequest(
        @NotBlank String fullName,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") Double gwa,
        @NotBlank String location,
        @NotBlank String incomeBracket,
        @NotNull EducationLevel educationLevel) { }
