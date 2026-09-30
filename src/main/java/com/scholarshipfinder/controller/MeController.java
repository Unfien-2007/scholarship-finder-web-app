package com.scholarshipfinder.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.scholarshipfinder.dto.ProfileUpdateRequest;
import com.scholarshipfinder.dto.UserProfileResponse;
import com.scholarshipfinder.exception.NotFoundException;
import com.scholarshipfinder.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
public class MeController {
    private final UserRepository userRepository;

    public MeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/me")
    public UserProfileResponse me(Principal principal) {
        var user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new NotFoundException("User not found"));
        return new UserProfileResponse(user.getEmail(), user.getFullName(), user.getGwa(), user.getLocation(),
            user.getIncomeBracket(), user.getEducationLevel());
    }

    @PutMapping("/api/me")
    public UserProfileResponse update(@Valid @RequestBody ProfileUpdateRequest request, Principal principal) {
        var user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new NotFoundException("User not found"));
        user.updateProfile(request.fullName(), request.gwa(), request.location(), request.incomeBracket(),
                request.educationLevel());
        var savedUser = userRepository.save(user);
        return new UserProfileResponse(savedUser.getEmail(), savedUser.getFullName(), savedUser.getGwa(),
            savedUser.getLocation(), savedUser.getIncomeBracket(), savedUser.getEducationLevel());
    }
}
