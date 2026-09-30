package com.scholarshipfinder.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "scholarships")
public class Scholarship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String provider;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false)
    private LocalDate deadline;
    @Column(nullable = false, length = 2000)
    private String requirements;
    @Column(nullable = false, length = 3000)
    private String benefits;
    @Column(nullable = false, length = 500)
    private String applicationPeriod;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EducationLevel educationLevel;
    @Column(nullable = false)
    private String location;
    private Double minimumGwa;
    private String incomeBracket;

    protected Scholarship() { }

    public Scholarship(String title, String provider, BigDecimal amount, LocalDate deadline, String requirements,
                       String benefits, String applicationPeriod, EducationLevel educationLevel, String location,
                       Double minimumGwa, String incomeBracket) {
        this.title = title;
        this.provider = provider;
        this.amount = amount;
        this.deadline = deadline;
        this.requirements = requirements;
        this.benefits = benefits;
        this.applicationPeriod = applicationPeriod;
        this.educationLevel = educationLevel;
        this.location = location;
        this.minimumGwa = minimumGwa;
        this.incomeBracket = incomeBracket;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getProvider() { return provider; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDeadline() { return deadline; }
    public String getRequirements() { return requirements; }
    public String getBenefits() { return benefits; }
    public String getApplicationPeriod() { return applicationPeriod; }
    public EducationLevel getEducationLevel() { return educationLevel; }
    public String getLocation() { return location; }
    public Double getMinimumGwa() { return minimumGwa; }
    public String getIncomeBracket() { return incomeBracket; }

    public void update(String title, String provider, BigDecimal amount, LocalDate deadline, String requirements,
                       String benefits, String applicationPeriod, EducationLevel educationLevel, String location,
                       Double minimumGwa, String incomeBracket) {
        this.title = title;
        this.provider = provider;
        this.amount = amount;
        this.deadline = deadline;
        this.requirements = requirements;
        this.benefits = benefits;
        this.applicationPeriod = applicationPeriod;
        this.educationLevel = educationLevel;
        this.location = location;
        this.minimumGwa = minimumGwa;
        this.incomeBracket = incomeBracket;
    }
}
