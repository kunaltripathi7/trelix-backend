package com.trelix.trelix_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateChannelRequest(
        UUID teamId, // Optional
        UUID projectId, // Optional

        @NotBlank(message = "Channel name cannot be empty")
        @Size(min = 3, max = 100, message = "Channel name must be between 3 and 100 characters")
        String name,

        @Size(max = 1000, message = "Channel description cannot exceed 1000 characters")
        String description,

        boolean isPrivate // Assuming this is a new field for ad-hoc channels
) {}
