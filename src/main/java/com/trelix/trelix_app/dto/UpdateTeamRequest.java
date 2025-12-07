package com.trelix.trelix_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTeamRequest(
    @NotBlank(message = "Team name cannot be blank.")
    @Size(min = 3, max = 100, message = "Team name must be between 3 and 100 characters.")
    String name,

    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    String description
) {}
