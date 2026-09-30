package com.scholarshipfinder.dto;

import com.scholarshipfinder.model.EducationLevel;
import jakarta.validation.constraints.*;

public record StudentProfileRequest(
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") Double gwa,
        @NotBlank String location,
        @NotBlank String incomeBracket,
        @NotNull EducationLevel educationLevel) { }
