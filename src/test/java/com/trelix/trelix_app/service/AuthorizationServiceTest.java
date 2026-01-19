package com.trelix.trelix_app.service;

import com.trelix.trelix_app.entity.TeamUser;
import com.trelix.trelix_app.enums.TeamRole;
import com.trelix.trelix_app.repository.ProjectMemberRepository;
import com.trelix.trelix_app.repository.TaskMemberRepository;
import com.trelix.trelix_app.repository.TaskRepository;
import com.trelix.trelix_app.repository.TeamUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private TeamUserRepository teamUserRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private TaskMemberRepository taskMemberRepository;

    @Mock
    private TaskRepository taskRepository;

    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        authorizationService = new AuthorizationService(
                teamUserRepository,
                projectMemberRepository,
                taskMemberRepository,
                taskRepository,
                Collections.emptyList(),
                Collections.emptyList());
    }

    @Test
    @DisplayName("verifyTeamMembership() should return TeamUser when user is a member")
    void verifyTeamMembership_whenUserIsMember_returnsTeamUser() {
        UUID teamId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TeamUser expectedTeamUser = TeamUser.builder()
                .id(new TeamUser.TeamUserId(userId, teamId))
                .role(TeamRole.MEMBER)
                .build();

        when(teamUserRepository.findById_TeamIdAndId_UserId(teamId, userId))
                .thenReturn(Optional.of(expectedTeamUser));

        TeamUser result = authorizationService.verifyTeamMembership(teamId, userId);

        assertNotNull(result);
        assertEquals(TeamRole.MEMBER, result.getRole());
        verify(teamUserRepository, times(1)).findById_TeamIdAndId_UserId(teamId, userId);
    }

    @Test
    @DisplayName("verifyTeamMembership() should throw AccessDeniedException when user is not a member")
    void verifyTeamMembership_whenUserNotMember_throwsAccessDeniedException() {
        UUID teamId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(teamUserRepository.findById_TeamIdAndId_UserId(teamId, userId))
                .thenReturn(Optional.empty());

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> authorizationService.verifyTeamMembership(teamId, userId));

        assertEquals("You are not a member of this team.", exception.getMessage());
        verify(teamUserRepository, times(1)).findById_TeamIdAndId_UserId(teamId, userId);
    }
}
