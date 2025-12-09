package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.RoleAssignmentRequest;
import com.trelix.trelix_app.dto.TeamDetailResponse;
import com.trelix.trelix_app.dto.UserResponse;

import java.util.UUID;

public interface AdminService {

    TeamDetailResponse transferTeamOwnership(UUID teamId, UUID newOwnerId, UUID requesterId);

    UserResponse assignGlobalRole(RoleAssignmentRequest request);
}
