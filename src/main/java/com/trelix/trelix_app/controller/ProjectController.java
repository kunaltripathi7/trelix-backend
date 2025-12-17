package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.AddProjectMemberRequest;
import com.trelix.trelix_app.dto.CreateProjectRequest;
import com.trelix.trelix_app.dto.ProjectDetailResponse;
import com.trelix.trelix_app.dto.ProjectMemberResponse;
import com.trelix.trelix_app.dto.ProjectResponse;
import com.trelix.trelix_app.dto.UpdateProjectMemberRoleRequest;
import com.trelix.trelix_app.dto.UpdateProjectRequest;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@Validated
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails  currentUser) {
        ProjectResponse projectResponse = projectService.createProject(request, currentUser.getId());
        return new ResponseEntity<>(projectResponse, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjectsByTeam(
            @RequestParam @NotNull UUID teamId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        List<ProjectResponse> projects = projectService.getProjectsByTeam(teamId, currentUser.getId());
        return ResponseEntity.ok(projects);
    }


    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponse> getProjectById(
            @PathVariable @NotNull UUID projectId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        ProjectDetailResponse projectDetail = projectService.getProjectById(projectId, currentUser.getId());
        return ResponseEntity.ok(projectDetail);
    }


    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable @NotNull UUID projectId,
            @Valid @RequestBody UpdateProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        ProjectResponse updatedProject = projectService.updateProject(projectId, request, currentUser.getId());
        return ResponseEntity.ok(updatedProject);
    }


    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable @NotNull UUID projectId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        projectService.deleteProject(projectId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<ProjectMemberResponse>> getProjectMembers(
            @PathVariable @NotNull UUID projectId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        List<ProjectMemberResponse> members = projectService.getProjectMembers(projectId, currentUser.getId());
        return ResponseEntity.ok(members);
    }


    @PostMapping("/{projectId}/members")
    public ResponseEntity<ProjectMemberResponse> addMember(
            @PathVariable @NotNull UUID projectId,
            @Valid @RequestBody AddProjectMemberRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        ProjectMemberResponse newMember = projectService.addMember(projectId, request, currentUser.getId());
        return new ResponseEntity<>(newMember, HttpStatus.CREATED);
    }


    @PutMapping("/{projectId}/members/{userId}")
    public ResponseEntity<ProjectMemberResponse> updateMemberRole(
            @PathVariable @NotNull UUID projectId,
            @PathVariable @NotNull UUID userId,
            @Valid @RequestBody UpdateProjectMemberRoleRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        ProjectMemberResponse updatedMember = projectService.updateMemberRole(projectId, userId, request.role(), currentUser.getId());
        return ResponseEntity.ok(updatedMember);
    }


    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable @NotNull UUID projectId,
            @PathVariable @NotNull UUID userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        projectService.removeMember(projectId, userId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
