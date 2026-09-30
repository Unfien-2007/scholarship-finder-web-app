package com.scholarshipfinder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scholarshipfinder.model.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudentIdOrderByAppliedAtDesc(Long studentId);
    boolean existsByStudentIdAndScholarshipId(Long studentId, Long scholarshipId);
    Optional<Application> findByStudentIdAndScholarshipId(Long studentId, Long scholarshipId);
}
