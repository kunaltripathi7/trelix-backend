package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.CreateProjectRequest;
import com.trelix.trelix_app.dto.ProjectDetailResponse;
import com.trelix.trelix_app.dto.ProjectMemberResponse;
import com.trelix.trelix_app.dto.ProjectResponse;
import com.trelix.trelix_app.dto.UpdateProjectRequest;
import com.trelix.trelix_app.dto.AddProjectMemberRequest;
import com.trelix.trelix_app.entity.Project;
import com.trelix.trelix_app.entity.ProjectMember;
import com.trelix.trelix_app.entity.Team;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.enums.ProjectRole;
import com.trelix.trelix_app.exception.ConflictException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ProjectMemberRepository;
import com.trelix.trelix_app.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final AuthorizationService authorizationService;
    private final TeamService teamService;
    private final UserService userService;

    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, UUID creatorId) {
        Team team = teamService.getTeamById(request.teamId());

        authorizationService.verifyTeamAdmin(request.teamId(), creatorId);

        Project project = Project.builder()
                .team(team)
                .name(request.name())
                .description(request.description())
                .build();
    
        Project savedProject = projectRepository.save(project);
    
        User creator = userService.findById(creatorId);

        ProjectMember projectMember = ProjectMember.builder()
                .id(new ProjectMember.ProjectMemberId(creatorId, savedProject.getId()))
                .user(creator)
                .project(savedProject)
                .role(ProjectRole.ADMIN)
                .build();

        projectMemberRepository.save(projectMember);

        return ProjectResponse.from(savedProject);
    }

    @Override
    @Transactional(readOnly = true) // hibernate doesn't compare to snapshot and uses resources. -> faster (readOnly = true)
    public List<ProjectResponse> getProjectsByTeam(UUID teamId, UUID requesterId) {
        authorizationService.verifyTeamMembership(teamId, requesterId);

        List<Project> projects = projectRepository.findByTeamId(teamId);

        return projects.stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDetailResponse getProjectById(UUID projectId, UUID requesterId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        authorizationService.verifyTeamMembership(project.getTeamId(), requesterId);

        List<ProjectMember> projectMembers = projectMemberRepository.findByIdProjectId(projectId);

        String teamName = teamService.getTeamById(project.getTeamId()).getName();

        return ProjectDetailResponse.from(project, teamName, projectMembers);
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(UUID projectId, UpdateProjectRequest request, UUID requesterId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        authorizationService.verifyProjectAdmin(projectId, requesterId);

        project.setName(request.name());
        project.setDescription(request.description());

        Project updatedProject = projectRepository.save(project);

        return ProjectResponse.from(updatedProject);
    }

    @Override
    @Transactional
    public void deleteProject(UUID projectId, UUID requesterId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        authorizationService.verifyProjectAdmin(projectId, requesterId);

        projectRepository.delete(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getProjectMembers(UUID projectId, UUID requesterId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        authorizationService.verifyTeamMembership(project.getTeamId(), requesterId);

        List<ProjectMember> projectMembers = projectMemberRepository.findByIdProjectId(projectId);

        return projectMembers.stream()
                .map(ProjectMemberResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectMemberResponse addMember(UUID projectId, AddProjectMemberRequest request, UUID requesterId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        authorizationService.verifyProjectAdmin(projectId, requesterId);

        authorizationService.verifyTeamMembership(project.getTeamId(), request.userId());

        if (projectMemberRepository.existsByIdProjectIdAndIdUserId(projectId, request.userId())) {
            throw new ConflictException("User " + request.userId() + " is already a member of project " + projectId, ErrorCode.INVALID_INPUT);
        }

        User userToAdd = userService.findById(request.userId());

        ProjectMember projectMember = ProjectMember.builder()
                .id(new ProjectMember.ProjectMemberId(request.userId(), projectId))
                .user(userToAdd)
                .project(project)
                .role(request.role())
                .build();

        ProjectMember savedProjectMember = projectMemberRepository.save(projectMember);

        return ProjectMemberResponse.from(savedProjectMember);
    }

    @Override
    @Transactional
    public ProjectMemberResponse updateMemberRole(UUID projectId, UUID userId, ProjectRole newRole, UUID requesterId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        authorizationService.verifyProjectAdmin(projectId, requesterId);

        ProjectMember projectMember = projectMemberRepository.findByIdProjectIdAndIdUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " is not a member of project " + projectId));

        if (projectMember.getRole() == ProjectRole.ADMIN && newRole == ProjectRole.MEMBER && userId.equals(requesterId)) {
            long adminCount = projectMemberRepository.countByIdProjectIdAndRole(projectId, ProjectRole.ADMIN);
            if (adminCount == 1) {
                throw new ConflictException("Cannot demote the last ADMIN of the project.", ErrorCode.INVALID_INPUT);
            }
        }

        projectMember.setRole(newRole);

        ProjectMember updatedProjectMember = projectMemberRepository.save(projectMember);

        return ProjectMemberResponse.from(updatedProjectMember);
    }

    @Override
    @Transactional
    public void removeMember(UUID projectId, UUID userId, UUID requesterId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        authorizationService.verifyProjectAdmin(projectId, requesterId);

        ProjectMember projectMember = projectMemberRepository.findByIdProjectIdAndIdUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " is not a member of project " + projectId));


        if (projectMember.getRole() == ProjectRole.ADMIN) {
            long adminCount = projectMemberRepository.countByIdProjectIdAndRole(projectId, ProjectRole.ADMIN);
            if (adminCount == 1) {
                throw new ConflictException("Cannot remove the last ADMIN of the project.", ErrorCode.INVALID_INPUT);
            }
        }

        projectMemberRepository.delete(projectMember);
    }
}
