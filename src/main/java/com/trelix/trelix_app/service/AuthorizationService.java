package com.trelix.trelix_app.service;

import com.trelix.trelix_app.entity.ProjectMember;
import com.trelix.trelix_app.entity.TeamUser;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ProjectMemberRepository;
import com.trelix.trelix_app.repository.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final TeamUserRepository teamUserRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public void checkIfUserIsAdminInTeam(UUID teamId, UUID userId) {
        TeamUser teamUser = teamUserRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
       if (!"ADMIN".equals(teamUser.getRole().toString())) throw new ResourceNotFoundException("User is not an admin.");
    }

    public void checkIfUserIsMemberInTeam(UUID teamId, UUID userId) {
        teamUserRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    public void checkIfUserIsMemberInProject(UUID projectId, UUID userId) {
        projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    public void checkIfUserIsAdminInProject(UUID projectId, UUID userId) {
        ProjectMember projectMember =  projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        if (!"ADMIN".equals(projectMember.getRole().toString()))
            throw new ResourceNotFoundException("User is not an admin.");
    }
}