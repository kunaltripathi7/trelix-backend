package com.trelix.trelix_app.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChannelDTO {
    private UUID id;
    private String name;
    private Boolean isPrivate;
    private LocalDateTime createdAt;
    private UUID teamId;
    private UUID projectId;
    private String description;
}