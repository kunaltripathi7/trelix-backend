package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.AddProjectMemberRequest;
import com.trelix.trelix_app.dto.CreateProjectRequest;
import com.trelix.trelix_app.dto.ProjectDetailResponse;
import com.trelix.trelix_app.dto.ProjectMemberResponse;
import com.trelix.trelix_app.dto.ProjectResponse;
import com.trelix.trelix_app.dto.UpdateProjectMemberRoleRequest;
import com.trelix.trelix_app.dto.UpdateProjectRequest;
import com.trelix.trelix_app.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@Validated
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID creatorId = UUID.fromString(jwt.getSubject());
        ProjectResponse projectResponse = projectService.createProject(request, creatorId);
        return new ResponseEntity<>(projectResponse, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjectsByTeam(
            @RequestParam @NotNull UUID teamId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        List<ProjectResponse> projects = projectService.getProjectsByTeam(teamId, requesterId);
        return ResponseEntity.ok(projects);
    }


    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponse> getProjectById(
            @PathVariable @NotNull UUID projectId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        ProjectDetailResponse projectDetail = projectService.getProjectById(projectId, requesterId);
        return ResponseEntity.ok(projectDetail);
    }


    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable @NotNull UUID projectId,
            @Valid @RequestBody UpdateProjectRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        ProjectResponse updatedProject = projectService.updateProject(projectId, request, requesterId);
        return ResponseEntity.ok(updatedProject);
    }


    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable @NotNull UUID projectId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        projectService.deleteProject(projectId, requesterId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<ProjectMemberResponse>> getProjectMembers(
            @PathVariable @NotNull UUID projectId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        List<ProjectMemberResponse> members = projectService.getProjectMembers(projectId, requesterId);
        return ResponseEntity.ok(members);
    }


    @PostMapping("/{projectId}/members")
    public ResponseEntity<ProjectMemberResponse> addMember(
            @PathVariable @NotNull UUID projectId,
            @Valid @RequestBody AddProjectMemberRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        ProjectMemberResponse newMember = projectService.addMember(projectId, request, requesterId);
        return new ResponseEntity<>(newMember, HttpStatus.CREATED);
    }


    @PutMapping("/{projectId}/members/{userId}")
    public ResponseEntity<ProjectMemberResponse> updateMemberRole(
            @PathVariable @NotNull UUID projectId,
            @PathVariable @NotNull UUID userId,
            @Valid @RequestBody UpdateProjectMemberRoleRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        ProjectMemberResponse updatedMember = projectService.updateMemberRole(projectId, userId, request.role(), requesterId);
        return ResponseEntity.ok(updatedMember);
    }


    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable @NotNull UUID projectId,
            @PathVariable @NotNull UUID userId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId = UUID.fromString(jwt.getSubject());
        projectService.removeMember(projectId, userId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
