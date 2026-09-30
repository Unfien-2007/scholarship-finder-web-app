package com.scholarshipfinder.repository;

import com.scholarshipfinder.model.EducationLevel;
import com.scholarshipfinder.model.Scholarship;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScholarshipRepository extends JpaRepository<Scholarship, Long> {
    List<Scholarship> findByEducationLevel(EducationLevel educationLevel);
    List<Scholarship> findByEducationLevelAndLocationContainingIgnoreCase(EducationLevel educationLevel, String location);
    List<Scholarship> findByLocationContainingIgnoreCase(String location);
}
