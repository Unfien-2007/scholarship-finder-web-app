package com.scholarshipfinder.controller;

import com.scholarshipfinder.dto.ScholarshipResponse;
import com.scholarshipfinder.dto.StudentProfileRequest;
import com.scholarshipfinder.model.EducationLevel;
import com.scholarshipfinder.service.ScholarshipService;
import jakarta.validation.Valid;
import java.util.List;
import java.security.Principal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scholarships")
public class ScholarshipController {
    private final ScholarshipService service;

    public ScholarshipController(ScholarshipService service) { this.service = service; }

    @GetMapping
    public List<ScholarshipResponse> search(@RequestParam(required = false) EducationLevel educationLevel,
                                            @RequestParam(required = false) String location) {
        return service.search(educationLevel, location);
    }

    @GetMapping("/{id}")
    public ScholarshipResponse getById(@PathVariable Long id) { return service.getById(id); }

    @PostMapping("/matches")
    public List<ScholarshipResponse> findMatches(@Valid @RequestBody StudentProfileRequest profile) {
        return service.findMatches(profile);
    }

    @GetMapping("/matches/me")
    public List<ScholarshipResponse> findMatchesForUser(Principal principal) {
        return service.findMatchesForUser(principal.getName());
    }
}
