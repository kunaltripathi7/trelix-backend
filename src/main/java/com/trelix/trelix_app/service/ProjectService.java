package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.*;
import com.trelix.trelix_app.enums.ProjectRole;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    ProjectResponse createProject(CreateProjectRequest request, UUID creatorId);

    List<ProjectResponse> getProjectsByTeam(UUID teamId, UUID requesterId);

    ProjectDetailResponse getProjectById(UUID projectId, UUID requesterId);

    ProjectResponse updateProject(UUID projectId, UpdateProjectRequest request, UUID requesterId);

    void deleteProject(UUID projectId, UUID requesterId);

    List<ProjectMemberResponse> getProjectMembers(UUID projectId, UUID requesterId);

    ProjectMemberResponse addMember(UUID projectId, AddProjectMemberRequest request, UUID requesterId);

    ProjectMemberResponse updateMemberRole(UUID projectId, UUID userId, ProjectRole newRole, UUID requesterId);

    void removeMember(UUID projectId, UUID userId, UUID requesterId);
}
