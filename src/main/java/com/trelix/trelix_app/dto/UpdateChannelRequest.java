package com.trelix.trelix_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateChannelRequest(
        @NotBlank(message = "Channel name cannot be empty")
        @Size(min = 3, max = 100, message = "Channel name must be between 3 and 100 characters")
        String name
) {}
