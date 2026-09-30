package com.scholarshipfinder.service;

import com.scholarshipfinder.dto.ScholarshipRequest;
import com.scholarshipfinder.dto.ScholarshipResponse;
import com.scholarshipfinder.dto.StudentProfileRequest;
import com.scholarshipfinder.exception.NotFoundException;
import com.scholarshipfinder.model.EducationLevel;
import com.scholarshipfinder.model.Scholarship;
import com.scholarshipfinder.repository.ScholarshipRepository;
import com.scholarshipfinder.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ScholarshipService {
    private final ScholarshipRepository repository;
    private final UserRepository userRepository;

    public ScholarshipService(ScholarshipRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ScholarshipResponse> search(EducationLevel educationLevel, String location) {
        List<Scholarship> scholarships;
        if (educationLevel != null && location != null && !location.isBlank()) {
            scholarships = repository.findByEducationLevelAndLocationContainingIgnoreCase(educationLevel, location);
        } else if (educationLevel != null) {
            scholarships = repository.findByEducationLevel(educationLevel);
        } else if (location != null && !location.isBlank()) {
            scholarships = repository.findByLocationContainingIgnoreCase(location);
        } else {
            scholarships = repository.findAll();
        }
        return scholarships.stream().filter(this::isOpen).map(ScholarshipResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ScholarshipResponse getById(Long id) {
        return ScholarshipResponse.from(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Scholarship not found: " + id)));
    }

    public ScholarshipResponse create(ScholarshipRequest request) {
        return ScholarshipResponse.from(repository.save(toEntity(request)));
    }

    public ScholarshipResponse update(Long id, ScholarshipRequest request) {
        Scholarship scholarship = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Scholarship not found: " + id));
        scholarship.update(request.title(), request.provider(), request.amount(), request.deadline(),
            request.requirements(), request.benefits(), request.applicationPeriod(), request.educationLevel(),
            request.location(), request.minimumGwa(),
                request.incomeBracket());
        return ScholarshipResponse.from(scholarship);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Scholarship not found: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ScholarshipResponse> findMatches(StudentProfileRequest profile) {
        return repository.findByEducationLevel(profile.educationLevel()).stream()
            .filter(this::isOpen)
            .filter(scholarship -> isLocationMatch(scholarship, profile.location()))
            .map(ScholarshipResponse::from)
                .filter(scholarship -> scholarship.minimumGwa() == null || profile.gwa() >= scholarship.minimumGwa())
                .filter(scholarship -> scholarship.incomeBracket() == null || scholarship.incomeBracket().equalsIgnoreCase(profile.incomeBracket()))
                .toList();
    }

            @Transactional(readOnly = true)
            public List<ScholarshipResponse> findMatchesForUser(String email) {
            var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
            return findMatches(new StudentProfileRequest(user.getGwa(), user.getLocation(),
                user.getIncomeBracket(), user.getEducationLevel()));
            }

    private Scholarship toEntity(ScholarshipRequest request) {
        return new Scholarship(request.title(), request.provider(), request.amount(), request.deadline(),
            request.requirements(), request.benefits(), request.applicationPeriod(), request.educationLevel(),
            request.location(), request.minimumGwa(), request.incomeBracket());
    }

    private boolean isOpen(Scholarship scholarship) {
        return !scholarship.getDeadline().isBefore(LocalDate.now());
    }

    private boolean isLocationMatch(Scholarship scholarship, String studentLocation) {
        String scholarshipLocation = scholarship.getLocation().toLowerCase();
        String normalizedStudentLocation = studentLocation.toLowerCase();
        return scholarshipLocation.contains("nationwide")
                || scholarshipLocation.contains(normalizedStudentLocation)
                || normalizedStudentLocation.contains(scholarshipLocation);
    }
}
