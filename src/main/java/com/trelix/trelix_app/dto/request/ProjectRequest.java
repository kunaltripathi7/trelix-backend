package com.trelix.trelix_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequest {
    @NotBlank(message = "Name is required.")
    @Size(min = 3, max = 15, message = "Name length can be between 3 and 15 characters.")
    private String name;

    @NotBlank(message = "Description is required.")
    private String description;

    private String status;
}




