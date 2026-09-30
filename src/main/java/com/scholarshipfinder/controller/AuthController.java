package com.scholarshipfinder.controller;

import com.scholarshipfinder.dto.AuthDtos;
import com.scholarshipfinder.exception.ConflictException;
import com.scholarshipfinder.model.Role;
import com.scholarshipfinder.model.User;
import com.scholarshipfinder.repository.UserRepository;
import com.scholarshipfinder.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public AuthDtos.LoginResponse login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return responseFor(userRepository.findByEmail(request.email()).orElseThrow());
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthDtos.LoginResponse signup(@Valid @RequestBody AuthDtos.SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("An account already exists for this email");
        }
        User user = userRepository.save(new User(request.email(), passwordEncoder.encode(request.password()),
                Role.ROLE_STUDENT, request.fullName(), request.gwa(), request.location(), request.incomeBracket(),
                request.educationLevel()));
        return responseFor(user);
    }

    private AuthDtos.LoginResponse responseFor(User user) {
        UserDetails principal = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword()).authorities(user.getRole().name()).build();
        return new AuthDtos.LoginResponse(jwtService.generateToken(principal), user.getRole().name(), user.getFullName());
    }
}
