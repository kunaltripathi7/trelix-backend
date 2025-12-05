package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.ProjectDetailResponse;
import com.trelix.trelix_app.dto.ProjectRequest;
import com.trelix.trelix_app.dto.ProjectResponse;
import com.trelix.trelix_app.entity.Project;
import com.trelix.trelix_app.entity.Team;
import com.trelix.trelix_app.enums.ProjectStatus;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ProjectRepository;
import com.trelix.trelix_app.repository.TeamRepository;
import com.trelix.trelix_app.util.AppMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final AuthorizationService authService;

    public ProjectDetailResponse createProject(ProjectRequest request, UUID teamId, UUID userId) {
        if (!authService.checkIfUserIsAdminInTeam(teamId, userId)) {
            throw new AccessDeniedException("You do not have permission to create a project in this team.");
        }
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .team(team)
                .status(ProjectStatus.NOT_STARTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        projectRepository.save(project);
        return AppMapper.convertToProjectDetailResponse(project);
    }

    public ProjectDetailResponse getProject(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        authService.checkProjectAccess(project.getTeam().getId(), projectId, userId);
        return AppMapper.convertToProjectDetailResponse(project);
    }

    public List<ProjectResponse> getProjects(UUID teamId, UUID userId) {
        authService.checkTeamAccess(teamId, userId);
        List<Project> projects;
        if (authService.checkIfUserIsAdminInTeam(teamId, userId)) {
            projects = projectRepository.findByTeamId(teamId);
        } else {
            projects = projectRepository.findByTeamIdAndProjectMembersUserId(teamId, userId);
        }
        return projects.stream().map(AppMapper::convertToProjectResponse).toList();
    }

    public ProjectDetailResponse updateProject(UUID projectId, ProjectRequest request, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        authService.checkProjectAdminAccess(project.getTeam().getId(), projectId, userId);

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            project.setStatus(ProjectStatus.valueOf(request.getStatus()));
        }
        project.setUpdatedAt(LocalDateTime.now());

        projectRepository.save(project);
        return AppMapper.convertToProjectDetailResponse(project);
    }

    public void deleteProject(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        authService.checkProjectAdminAccess(project.getTeam().getId(), projectId, userId);
        projectRepository.deleteById(projectId);
    }
}
