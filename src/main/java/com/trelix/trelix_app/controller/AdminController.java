package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.RoleAssignmentRequest;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')") // it adds ROLE_Automaticaly
    @PostMapping("/assign-role")
    public ResponseEntity<String> assignRole(@Valid @RequestBody RoleAssignmentRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(request.getNewRole());
        userRepository.save(user);

        return ResponseEntity.ok("Role updated to " + request.getNewRole());
    }

}
