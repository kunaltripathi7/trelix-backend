package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.request.AddProjectMemberRequest;
import com.trelix.trelix_app.dto.request.CreateProjectRequest;
import com.trelix.trelix_app.dto.response.ProjectDetailResponse;
import com.trelix.trelix_app.dto.response.ProjectMemberResponse;
import com.trelix.trelix_app.dto.response.ProjectResponse;
import com.trelix.trelix_app.dto.request.UpdateProjectMemberRoleRequest;
import com.trelix.trelix_app.dto.request.UpdateProjectRequest;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.AuthorizationService;
import com.trelix.trelix_app.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/v1/projects")
@Validated
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management within teams")
public class ProjectController {

    private final ProjectService projectService;
    private final AuthorizationService authorizationService;

    @PostMapping
    @Operation(summary = "Create project", description = "Create a new project within a team")
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ProjectResponse projectResponse = projectService.createProject(request, currentUser.getId());
        return new ResponseEntity<>(projectResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get projects by team", description = "Get all projects in a team")
    public ResponseEntity<List<ProjectResponse>> getProjectsByTeam(
            @RequestParam @NotNull UUID teamId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<ProjectResponse> projects = projectService.getProjectsByTeam(teamId, currentUser.getId());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Get project details", description = "Get detailed information about a project")
    public ResponseEntity<ProjectDetailResponse> getProjectById(
            @PathVariable @NotNull UUID projectId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        UUID requesterId = currentUser.getId();
        authorizationService.verifyProjectMembership(projectId, requesterId);
        ProjectDetailResponse projectDetail = projectService.getProjectById(projectId);
        return ResponseEntity.ok(projectDetail);
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "Update project", description = "Update project name or description")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable @NotNull UUID projectId,
            @Valid @RequestBody UpdateProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ProjectResponse updatedProject = projectService.updateProject(projectId, request, currentUser.getId());
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "Delete project", description = "Delete a project and all its tasks")
    public ResponseEntity<Void> deleteProject(
            @PathVariable @NotNull UUID projectId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        projectService.deleteProject(projectId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{projectId}/members")
    @Operation(summary = "Get project members", description = "Get list of members in a project")
    public ResponseEntity<List<ProjectMemberResponse>> getProjectMembers(
            @PathVariable @NotNull UUID projectId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<ProjectMemberResponse> members = projectService.getProjectMembers(projectId, currentUser.getId());
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{projectId}/members")
    @Operation(summary = "Add project member", description = "Add a team member to the project")
    public ResponseEntity<ProjectMemberResponse> addMember(
            @PathVariable @NotNull UUID projectId,
            @Valid @RequestBody AddProjectMemberRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ProjectMemberResponse newMember = projectService.addMember(projectId, request, currentUser.getId());
        return new ResponseEntity<>(newMember, HttpStatus.CREATED);
    }

    @PutMapping("/{projectId}/members/{userId}")
    @Operation(summary = "Update member role", description = "Change a project member's role")
    public ResponseEntity<ProjectMemberResponse> updateMemberRole(
            @PathVariable @NotNull UUID projectId,
            @PathVariable @NotNull UUID userId,
            @Valid @RequestBody UpdateProjectMemberRoleRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ProjectMemberResponse updatedMember = projectService.updateMemberRole(projectId, userId, request.role(),
                currentUser.getId());
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    @Operation(summary = "Remove project member", description = "Remove a user from the project")
    public ResponseEntity<Void> removeMember(
            @PathVariable @NotNull UUID projectId,
            @PathVariable @NotNull UUID userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        projectService.removeMember(projectId, userId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
