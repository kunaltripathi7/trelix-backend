package com.trelix.trelix_app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignTaskMemberRequest {

    @NotNull(message = "User ID cannot be null")
    private UUID userId;


}
