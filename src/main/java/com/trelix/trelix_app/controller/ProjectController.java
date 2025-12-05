package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.ProjectDetailResponse;
import com.trelix.trelix_app.dto.ProjectRequest;
import com.trelix.trelix_app.dto.ProjectResponse;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/teams/{teamId}/projects")
    public ResponseEntity<ProjectDetailResponse> createProject(@PathVariable UUID teamId,
                                                               @Valid @RequestBody ProjectRequest request,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProjectDetailResponse project = projectService.createProject(request, teamId, userDetails.getId());
        return new ResponseEntity<>(project, HttpStatus.CREATED);
    }

    @GetMapping("/teams/{teamId}/projects")
    public ResponseEntity<List<ProjectResponse>> getAllProjects(@PathVariable UUID teamId,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ProjectResponse> projects = projectService.getProjects(teamId, userDetails.getId());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ProjectDetailResponse> getProjectDetails(@PathVariable UUID projectId,
                                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProjectDetailResponse project = projectService.getProject(projectId, userDetails.getId());
        return ResponseEntity.ok(project);
    }

    @PutMapping("/projects/{projectId}")
    public ResponseEntity<ProjectDetailResponse> updateProject(@PathVariable UUID projectId,
                                                               @Valid @RequestBody ProjectRequest request,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProjectDetailResponse updatedProject = projectService.updateProject(projectId, request, userDetails.getId());
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        projectService.deleteProject(projectId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
