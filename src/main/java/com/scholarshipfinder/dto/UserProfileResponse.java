package com.scholarshipfinder.dto;

import com.scholarshipfinder.model.EducationLevel;

public record UserProfileResponse(String email, String fullName, Double gwa, String location,
                                  String incomeBracket, EducationLevel educationLevel) { }
