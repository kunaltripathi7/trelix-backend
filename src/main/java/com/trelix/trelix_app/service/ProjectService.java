package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.ProjectDetailResponse;
import com.trelix.trelix_app.dto.ProjectResponse;
import com.trelix.trelix_app.dto.ProjectRequest;
import com.trelix.trelix_app.entity.Project;
import com.trelix.trelix_app.entity.Team;
import com.trelix.trelix_app.enums.ProjectStatus;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ProjectRepository;
import com.trelix.trelix_app.repository.TeamRepository;
import com.trelix.trelix_app.util.AppMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    public ProjectDetailResponse createProject(ProjectRequest request, UUID teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() ->  new ResourceNotFoundException("Team doesn't exist"));
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

    public List<ProjectResponse> getProjects(UUID teamId, UUID userId) {
        List<Project> projects = projectRepository.findByTeamId(teamId);
        return projects.stream().map(AppMapper::convertToProjectResponse).toList();
    }

//    public ProjectDetailResponse getProject(UUID projectId) {
//        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project doesn't exist."));
//        return AppMapper.convertToProjectDetailResponse(project);
//    }


    public List<ProjectResponse> getProjects(UUID teamId, UUID userId) {
        boolean isTeamAdmin = teamUserRepository.existsByTeamIdAndUserIdAndRole(teamId, userId, Role.ADMIN);
        List<Project> projects;
        if (isTeamAdmin) {
            projects = projectRepository.findByTeamId(teamId);
        } else {
            projects = projectRepository.findByTeamIdAndUserIsMember(teamId, userId);
        }
        return projects.stream().map(AppMapper::convertToProjectResponse).toList();
    }


    public ProjectDetailResponse updateProject(UUID projectId, ProjectRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project doesn't exist."));

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            project.setStatus(ProjectStatus.valueOf(request.getStatus())); // method to convert to enum
        }
        project.setUpdatedAt(LocalDateTime.now());

        projectRepository.save(project);
        return AppMapper.convertToProjectDetailResponse(project);
    }

    public void deleteProject(UUID projectId) {
        projectRepository.deleteById(projectId);
    }

}
