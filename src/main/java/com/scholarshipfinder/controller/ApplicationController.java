package com.scholarshipfinder.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scholarshipfinder.dto.ApplicationResponse;
import com.scholarshipfinder.dto.ApplicationStatusRequest;
import com.scholarshipfinder.service.ApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    private final ApplicationService service;

    public ApplicationController(ApplicationService service) { this.service = service; }

    @PostMapping("/{scholarshipId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse apply(@PathVariable Long scholarshipId, Principal principal) {
        return service.apply(scholarshipId, principal.getName());
    }

    @GetMapping("/me")
    public List<ApplicationResponse> mine(Principal principal) { return service.findMine(principal.getName()); }

    @PatchMapping("/{applicationId}/status")
    public ApplicationResponse changeStatus(@PathVariable Long applicationId,
                                             @Valid @RequestBody ApplicationStatusRequest request,
                                             Principal principal) {
        return service.changeStudentStatus(applicationId, principal.getName(), request.status());
    }
}
