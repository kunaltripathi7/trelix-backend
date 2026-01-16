package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.request.RoleAssignmentRequest;
import com.trelix.trelix_app.dto.response.UserResponse;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                updatedUser.getCreatedAt());
    }
}




