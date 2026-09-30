package com.scholarshipfinder.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String fullName;
    private Double gwa;
    private String location;
    private String incomeBracket;
    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;

    protected User() { }

    public User(String email, String password, Role role, String fullName, Double gwa, String location,
                String incomeBracket, EducationLevel educationLevel) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.gwa = gwa;
        this.location = location;
        this.incomeBracket = incomeBracket;
        this.educationLevel = educationLevel;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public String getFullName() { return fullName; }
    public Double getGwa() { return gwa; }
    public String getLocation() { return location; }
    public String getIncomeBracket() { return incomeBracket; }
    public EducationLevel getEducationLevel() { return educationLevel; }

    public void updateProfile(String fullName, Double gwa, String location, String incomeBracket,
                              EducationLevel educationLevel) {
        this.fullName = fullName;
        this.gwa = gwa;
        this.location = location;
        this.incomeBracket = incomeBracket;
        this.educationLevel = educationLevel;
    }
}
