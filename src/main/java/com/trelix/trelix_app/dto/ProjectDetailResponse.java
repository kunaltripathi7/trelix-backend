package com.trelix.trelix_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDetailResponse {
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private String status;
    private List<ChannelDTO> channels;
    private List<TaskDTO> tasks;
}
