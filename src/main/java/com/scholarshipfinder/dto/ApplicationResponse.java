package com.scholarshipfinder.dto;

import com.scholarshipfinder.model.Application;
import com.scholarshipfinder.model.ApplicationStatus;
import java.time.LocalDateTime;

public record ApplicationResponse(Long id, Long scholarshipId, String scholarshipTitle,
                                  ApplicationStatus status, LocalDateTime appliedAt) {
    public static ApplicationResponse from(Application application) {
        return new ApplicationResponse(application.getId(), application.getScholarship().getId(),
                application.getScholarship().getTitle(), application.getStatus(), application.getAppliedAt());
    }
}
