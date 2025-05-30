package com.trelix.trelix_app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ChannelRequest {
    @NotBlank(message = "Channel name cannot be blank")
    @Max(value=100, message = "Channel name cannot exceed 100 characters")
    private String name;
    @NotNull(message = "Channel privacy status cannot be null")
    private Boolean isPrivate;
    private String description;
}
