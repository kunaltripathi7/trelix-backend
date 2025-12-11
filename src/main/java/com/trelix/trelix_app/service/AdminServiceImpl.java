package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.RoleAssignmentRequest;
import com.trelix.trelix_app.dto.TeamDetailResponse;
import com.trelix.trelix_app.dto.UserResponse;
import com.trelix.trelix_app.entity.Team;
import com.trelix.trelix_app.entity.TeamUser;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.enums.TeamRole;
import com.trelix.trelix_app.exception.ForbiddenException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.TeamRepository;
import com.trelix.trelix_app.repository.TeamUserRepository;
import com.trelix.trelix_app.repository.UserRepository;
import com.trelix.trelix_app.util.AppMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {


    private final UserRepository userRepository;


    @Override
    @Transactional
    public UserResponse assignGlobalRole(RoleAssignmentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        user.setRole(request.getRole());
        User updatedUser = userRepository.save(user);

        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getCreatedAt()
        );
    }
}
