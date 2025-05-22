package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.ProjectDetailResponse;
import com.trelix.trelix_app.dto.ProjectRequest;

import com.trelix.trelix_app.dto.ProjectResponse;
import com.trelix.trelix_app.enums.Role;
import com.trelix.trelix_app.security.CustomUserDetails;
import com.trelix.trelix_app.service.AuthorizationService;
import com.trelix.trelix_app.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @Autowired
    AuthorizationService authService;

    @PostMapping("/teams/{teamId}/projects")
    public ResponseEntity<ProjectDetailResponse> createProject(@PathVariable UUID teamId, @Valid @RequestBody ProjectRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!authService.checkIfUserIsAdminInTeam(teamId, userDetails.getId())) throw new AccessDeniedException("You are not authorized to create a project in this team.");
        ProjectDetailResponse project = projectService.createProject(request, teamId);
        return new ResponseEntity<>(project, HttpStatus.CREATED);
    }

    @GetMapping("/teams/{teamId}/projects")
    public ResponseEntity<List<ProjectResponse>> getAllProjects(@PathVariable UUID teamId, CustomUserDetails userDetails) {
        List<ProjectResponse> projects;
        if (authService.checkIfUserIsAdminInTeam(teamId, userDetails.getId())) {
            projects = projectService.getProjects(teamId, userDetails.getId(), true);
        } else if (authService.checkIfUserIsMemberInTeam(teamId, userDetails.getId())) {
            projects = projectService.getProjects(teamId, userDetails.getId(), false);
        }
        else projects = new ArrayList<>();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @GetMapping("/teams/{teamId}/projects/{projectId}")
    public ResponseEntity<ProjectDetailResponse> getProjectDetails(@PathVariable UUID teamId, @PathVariable UUID projectId, CustomUserDetails userDetails) {
        if (!authService.checkIfUserIsMemberInProject(projectId, userDetails.getId())) {
            throw new AccessDeniedException("You are not authorized to view this project.");
        }
        ProjectDetailResponse project = projectService.getProject(projectId);
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @PutMapping("/teams/{teamId}/projects/{projectId}")
    public ResponseEntity<ProjectDetailResponse> updateProject(
            @PathVariable UUID teamId,
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!authService.checkIfUserIsAdminInProject(projectId, userDetails.getId())) {
            throw new AccessDeniedException("You are not authorized to update this project.");
        }
        ProjectDetailResponse updatedProject = projectService.updateProject(projectId, request);
        return new ResponseEntity<>(updatedProject, HttpStatus.OK);
    }

    @DeleteMapping("/teams/{teamId}/projects/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable UUID teamId, @PathVariable UUID projectId, CustomUserDetails userDetails) {
        if (!authService.checkIfUserIsAdminInProject(projectId, userDetails.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this project.");
        }
        projectService.deleteProject(projectId);
        return new ResponseEntity<>("Project has been deleted.", HttpStatus.NO_CONTENT);
    }
}
