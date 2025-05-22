package com.trelix.trelix_app.dto;

import com.trelix.trelix_app.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusChangeDTO {
    private String oldStatus;
    private String newStatus;
    private String changedByName;
    private LocalDateTime changedAt;
}
