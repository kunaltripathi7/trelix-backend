package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.request.RoleAssignmentRequest;
import com.trelix.trelix_app.dto.response.UserResponse;

import java.util.UUID;

public interface AdminService {
    UserResponse assignGlobalRole(RoleAssignmentRequest request);
}




