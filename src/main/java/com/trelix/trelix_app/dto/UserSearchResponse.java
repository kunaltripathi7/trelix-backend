package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.entity.User;

import java.util.UUID;

public record UserSearchResponse(
        UUID id,
        String name,
        String email,
        String profilePictureUrl // nullable
) {
    public static UserSearchResponse from(User user) {
        return new UserSearchResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                null // profilePictureUrl not available in User entity for now
        );
    }
}
