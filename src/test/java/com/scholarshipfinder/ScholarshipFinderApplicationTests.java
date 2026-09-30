package com.scholarshipfinder;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.scholarshipfinder.controller.AuthController;
import com.scholarshipfinder.controller.MeController;
import com.scholarshipfinder.dto.AuthDtos;
import com.scholarshipfinder.dto.ProfileUpdateRequest;
import com.scholarshipfinder.dto.StudentProfileRequest;
import com.scholarshipfinder.model.EducationLevel;
import com.scholarshipfinder.service.ScholarshipService;

@SpringBootTest
class ScholarshipFinderApplicationTests {
    @Autowired
    private ScholarshipService scholarshipService;

    @Autowired
    private AuthController authController;

    @Autowired
    private MeController meController;

    @Test
    void seededScholarshipsMatchStudentProfile() {
        var matches = scholarshipService.findMatches(new StudentProfileRequest(85.0, "Manila", "LOW", EducationLevel.UNDERGRADUATE));
        assertThat(matches).extracting("title").contains("DOST Junior Level Science Scholarship (JLSS)");
    }

    @Test
    void studentCanCreateAnAccountAndReceiveToken() {
        String email = "new-student-" + System.nanoTime() + "@example.com";
        var response = authController.signup(new AuthDtos.SignupRequest(
                "New Student", email, "password123", 90.0, "Manila", "LOW", EducationLevel.UNDERGRADUATE));

        assertThat(response.token()).isNotBlank();
        assertThat(response.role()).isEqualTo("ROLE_STUDENT");
    }

    @Test
    void studentCanUpdateProfile() {
        var response = meController.update(
                new ProfileUpdateRequest("Jamie Updated", 94.0, "Quezon City", "LOW", EducationLevel.UNDERGRADUATE),
                () -> "student@example.com");

        assertThat(response.fullName()).isEqualTo("Jamie Updated");
        assertThat(response.gwa()).isEqualTo(94.0);
        assertThat(response.location()).isEqualTo("Quezon City");
    }
}
