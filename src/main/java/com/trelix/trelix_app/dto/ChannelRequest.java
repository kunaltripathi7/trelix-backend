package com.trelix.trelix_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ChannelRequest {
    @NotBlank(message = "Channel name cannot be blank")
    @Size(max = 100, message = "Channel name cannot exceed 100 characters")
    private String name;
    @NotNull(message = "Channel privacy status cannot be null")
    private Boolean isPrivate;
    private String description;
    private UUID projectId;
    @NotNull(message = "Team ID cannot be null")
    private UUID teamId;
}
