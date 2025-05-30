package com.trelix.trelix_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetailsDTO {
    private UUID id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID assignedToId;
    private String assignedToName;
    private List<TaskCommentDTO> comments;
    private List<AttachmentDTO> attachments;
    private List<TaskStatusChangeDTO> statusChanges;
    private List<EventDTO> events;
}
