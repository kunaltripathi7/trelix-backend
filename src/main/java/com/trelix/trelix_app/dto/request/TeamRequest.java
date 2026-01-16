package com.trelix.trelix_app.dto.request;

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

    private String description;
}




