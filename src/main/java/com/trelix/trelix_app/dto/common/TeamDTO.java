package com.trelix.trelix_app.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {
    private UUID id;
    private String name;
    private String description;
    private long memberCount;
    private LocalDateTime createdAt;
    private String currentUserRole;
}




