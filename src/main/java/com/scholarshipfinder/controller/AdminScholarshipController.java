package com.scholarshipfinder.controller;

import com.scholarshipfinder.dto.ScholarshipRequest;
import com.scholarshipfinder.dto.ScholarshipResponse;
import com.scholarshipfinder.service.ScholarshipService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/scholarships")
@PreAuthorize("hasRole('ADMIN')")
public class AdminScholarshipController {
    private final ScholarshipService service;

    public AdminScholarshipController(ScholarshipService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScholarshipResponse create(@Valid @RequestBody ScholarshipRequest request) { return service.create(request); }

    @PutMapping("/{id}")
    public ScholarshipResponse update(@PathVariable Long id, @Valid @RequestBody ScholarshipRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
