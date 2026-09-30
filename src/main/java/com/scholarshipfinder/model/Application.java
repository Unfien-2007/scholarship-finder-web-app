package com.scholarshipfinder.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "applications", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "scholarship_id"}))
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "scholarship_id")
    private Scholarship scholarship;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;
    @Column(nullable = false)
    private LocalDateTime appliedAt;

    protected Application() { }

    public Application(User student, Scholarship scholarship) {
        this.student = student;
        this.scholarship = scholarship;
        this.status = ApplicationStatus.PENDING;
        this.appliedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getStudent() { return student; }
    public Scholarship getScholarship() { return scholarship; }
    public ApplicationStatus getStatus() { return status; }
    public LocalDateTime getAppliedAt() { return appliedAt; }

    public void changeStudentStatus(ApplicationStatus newStatus) {
        this.status = newStatus;
    }
}
