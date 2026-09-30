package com.scholarshipfinder.dto;

import com.scholarshipfinder.model.EducationLevel;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ScholarshipRequest(
        @NotBlank String title,
        @NotBlank String provider,
        @NotNull @Positive BigDecimal amount,
        @NotNull @FutureOrPresent LocalDate deadline,
        @NotBlank String requirements,
        @NotBlank String benefits,
        @NotBlank String applicationPeriod,
        @NotNull EducationLevel educationLevel,
        @NotBlank String location,
        @DecimalMin("0.0") @DecimalMax("100.0") Double minimumGwa,
        String incomeBracket) { }
