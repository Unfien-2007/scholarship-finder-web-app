package com.scholarshipfinder.dto;

import com.scholarshipfinder.model.ApplicationStatus;

import jakarta.validation.constraints.NotNull;

public record ApplicationStatusRequest(@NotNull ApplicationStatus status) { }