package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.request.RoleAssignmentRequest;
import com.trelix.trelix_app.dto.response.UserResponse;
import com.trelix.trelix_app.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin")
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign-role")
    public ResponseEntity<UserResponse> assignRole(@Valid @RequestBody RoleAssignmentRequest request) {
        UserResponse updatedUser = adminService.assignGlobalRole(request);
        return ResponseEntity.ok(updatedUser);
    }
}
