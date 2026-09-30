package com.scholarshipfinder.config;

import com.scholarshipfinder.model.*;
import com.scholarshipfinder.repository.ScholarshipRepository;
import com.scholarshipfinder.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedData(UserRepository users, ScholarshipRepository scholarships, PasswordEncoder encoder) {
        return args -> {
            if (users.count() == 0) {
                users.save(new User("student@example.com", encoder.encode("student123"), Role.ROLE_STUDENT,
                    "Jamie Student", 90.0, "Manila", "LOW", EducationLevel.UNDERGRADUATE));
                users.save(new User("admin@example.com", encoder.encode("admin123"), Role.ROLE_ADMIN,
                    "Alex Administrator", null, null, null, null));
            }
            if (scholarships.count() == 0) {
                scholarships.save(new Scholarship(
                    "DOST-SEI Undergraduate Scholarships (RA 7687 & Merit)",
                    "Department of Science and Technology - Science Education Institute (DOST-SEI)",
                    new BigDecimal("8000"), LocalDate.of(2026, 12, 31),
                    "Natural-born Filipino citizen of good moral character.\n"
                        + "STEM strand graduate, or top 5% of the non-STEM graduating class.\n"
                        + "Must enroll in a DOST-SEI priority Science and Technology degree program.\n"
                        + "Documentary requirements include a birth certificate, latest Income Tax Return (ITR) of parents or guardians, and a certificate of good health.",
                    "PHP 8,000 monthly living allowance.\n"
                        + "PHP 40,000 tuition fee subsidy per academic year.\n"
                        + "PHP 10,000 book allowance per year.\n"
                        + "PHP 1,000 clothing allowance in the first semester.\n"
                        + "Economy-class roundtrip transportation allowance when studying outside the home province.",
                    "Typically opens October to December of the preceding academic year.",
                    EducationLevel.SENIOR_HIGH_SCHOOL, "Nationwide, Philippines", null, "LOW"));
                scholarships.save(new Scholarship(
                    "CHED Merit Scholarship Program (CMSP)",
                    "Commission on Higher Education (CHED)",
                    new BigDecimal("120000"), LocalDate.of(2027, 5, 31),
                    "Filipino citizen and high school graduate.\n"
                        + "General Weighted Average (GWA) of at least 96% for Full Merit, or 93% to 95.99% for Half Merit.\n"
                        + "Combined annual gross income of parents or guardians must not exceed PHP 400,000.\n"
                        + "Must enroll in CHED-recognized priority degree programs.",
                    "Up to PHP 120,000 per year for Full Merit scholars, covering tuition and living stipends.",
                    "Varies by region, typically opening between February and May.",
                    EducationLevel.SENIOR_HIGH_SCHOOL, "Nationwide, Philippines", 93.0, "LOW"));
                scholarships.save(new Scholarship(
                    "SM Foundation College Scholarship",
                    "SM Foundation",
                    new BigDecimal("100000"), LocalDate.of(2027, 3, 31),
                    "Filipino citizen with a household income of at most PHP 250,000 per year.\n"
                        + "GWA of at least 92% or equivalent in Grade 12 first semester.\n"
                        + "Must hold a DepEd voucher and have completed Grade 10 in a public high school, or a private high school with a voucher.\n"
                        + "Must enroll in priority programs, mostly IT, Engineering, Accounting, and Education.",
                    "100% college tuition fee coverage.\n"
                        + "Monthly living allowance.\n"
                        + "Exclusive job offers with the SM Group upon graduation.",
                    "Usually opens January to March every year.",
                    EducationLevel.SENIOR_HIGH_SCHOOL, "Nationwide, Philippines", 92.0, "LOW"));
                scholarships.save(new Scholarship(
                    "DOST Junior Level Science Scholarship (JLSS)",
                    "Department of Science and Technology - Science Education Institute (DOST-SEI)",
                    new BigDecimal("8000"), LocalDate.of(2027, 4, 30),
                    "Regular second-year college student enrolled in a DOST-SEI priority S&T course.\n"
                        + "GWA of at least 83% or equivalent with no failing grades in previous semesters.\n"
                        + "Must pass the JLSS Qualifying Examination.\n"
                        + "Must commit to return service in the Philippines by teaching STEM in high schools or working in the country's S&T sector.",
                    "PHP 7,000 to PHP 8,000 monthly living allowance.\n"
                        + "PHP 40,000 tuition fee subsidy per academic year.\n"
                        + "PHP 10,000 book allowance per year.\n"
                        + "PHP 10,000 thesis allowance.",
                    "Typically opens February to April of the student's second year in college.",
                    EducationLevel.UNDERGRADUATE, "Nationwide, Philippines", 83.0, null));
                scholarships.save(new Scholarship(
                    "DOST-ASTHRDP Graduate Scholarship",
                    "Department of Science and Technology - Science Education Institute (DOST-SEI)",
                    new BigDecimal("38000"), LocalDate.of(2027, 7, 31),
                    "Filipino citizen not more than 50 years old.\n"
                        + "Must pass the admission requirements for graduate studies at an NSC member-university.\n"
                        + "Must pursue a full-time MS or PhD degree in DOST-identified priority S&T fields.\n"
                        + "Cannot engage in full-time employment while on scholarship; employed scholars must take a formal leave of absence.",
                    "Actual tuition and other school fees.\n"
                        + "Monthly stipend of PHP 30,000 for MS or PHP 38,000 for PhD.\n"
                        + "PHP 20,000 book allowance per academic year.\n"
                        + "Thesis or dissertation grant of PHP 60,000 for MS or PHP 100,000 for PhD.\n"
                        + "Research grant up to PHP 114,000 for MS or PHP 253,000 for PhD.",
                    "Typical deadlines are May to July for the first semester and October to November for the second semester.",
                    EducationLevel.GRADUATE, "Nationwide, Philippines", null, null));
            }
        };
    }
}
