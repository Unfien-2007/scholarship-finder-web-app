package com.scholarshipfinder.dto;

import com.scholarshipfinder.model.EducationLevel;
import com.scholarshipfinder.model.Scholarship;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ScholarshipResponse(Long id, String title, String provider, BigDecimal amount, LocalDate deadline,
                                  String requirements, String benefits, String applicationPeriod,
                                  EducationLevel educationLevel, String location,
                                  Double minimumGwa, String incomeBracket) {
    public static ScholarshipResponse from(Scholarship scholarship) {
        return new ScholarshipResponse(scholarship.getId(), scholarship.getTitle(), scholarship.getProvider(),
                scholarship.getAmount(), scholarship.getDeadline(), scholarship.getRequirements(),
                scholarship.getBenefits(), scholarship.getApplicationPeriod(),
                scholarship.getEducationLevel(), scholarship.getLocation(), scholarship.getMinimumGwa(),
                scholarship.getIncomeBracket());
    }
}
