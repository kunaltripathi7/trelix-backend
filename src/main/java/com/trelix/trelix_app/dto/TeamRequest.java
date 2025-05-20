package com.trelix.trelix_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequest {
    @NotBlank(message = "Team name is required")
    private String name;

    @NotBlank(message = "Team description is required")
    private String description;
}
