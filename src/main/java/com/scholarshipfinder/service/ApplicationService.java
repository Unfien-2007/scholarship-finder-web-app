package com.scholarshipfinder.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scholarshipfinder.dto.ApplicationResponse;
import com.scholarshipfinder.exception.ConflictException;
import com.scholarshipfinder.exception.NotFoundException;
import com.scholarshipfinder.model.Application;
import com.scholarshipfinder.model.ApplicationStatus;
import com.scholarshipfinder.model.Scholarship;
import com.scholarshipfinder.model.User;
import com.scholarshipfinder.repository.ApplicationRepository;
import com.scholarshipfinder.repository.ScholarshipRepository;
import com.scholarshipfinder.repository.UserRepository;

@Service
@Transactional
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ScholarshipRepository scholarshipRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository, ScholarshipRepository scholarshipRepository,
                              UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.scholarshipRepository = scholarshipRepository;
        this.userRepository = userRepository;
    }

    public ApplicationResponse apply(Long scholarshipId, String email) {
        User student = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new NotFoundException("Scholarship not found: " + scholarshipId));
        var existing = applicationRepository.findByStudentIdAndScholarshipId(student.getId(), scholarshipId);
        if (existing.isPresent() && existing.get().getStatus() == ApplicationStatus.WITHDRAWN) {
            existing.get().changeStudentStatus(ApplicationStatus.PENDING);
            return ApplicationResponse.from(existing.get());
        }
        if (existing.isPresent()) {
            throw new ConflictException("You have already applied to this scholarship");
        }
        return ApplicationResponse.from(applicationRepository.save(new Application(student, scholarship)));
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> findMine(String email) {
        User student = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        return applicationRepository.findByStudentIdOrderByAppliedAtDesc(student.getId()).stream()
                .map(ApplicationResponse::from).toList();
    }

    public ApplicationResponse changeStudentStatus(Long applicationId, String email, ApplicationStatus newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found: " + applicationId));
        if (!application.getStudent().getEmail().equalsIgnoreCase(email)) {
            throw new NotFoundException("Application not found: " + applicationId);
        }
        if (application.getStatus() != ApplicationStatus.PENDING
                || (newStatus != ApplicationStatus.WITHDRAWN && newStatus != ApplicationStatus.RESULT_RECEIVED)) {
            throw new ConflictException("Only pending applications can be withdrawn or marked as a received result");
        }
        application.changeStudentStatus(newStatus);
        return ApplicationResponse.from(applicationRepository.save(application));
    }

}
