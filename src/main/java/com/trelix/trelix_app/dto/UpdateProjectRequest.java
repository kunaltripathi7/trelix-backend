package com.trelix.trelix_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProjectRequest(
        @NotBlank(message = "Project name cannot be empty")
        @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters")
        String name,

        @Size(max = 1000, message = "Project description cannot exceed 1000 characters")
        String description
) {}
