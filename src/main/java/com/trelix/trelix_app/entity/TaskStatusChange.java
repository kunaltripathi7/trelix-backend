package com.trelix.trelix_app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task_status_changes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatusChange {
    @Id @GeneratedValue
    private UUID id;

    @ManyToOne @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    private String previousStatus;
    private String newStatus;

    @ManyToOne @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    private LocalDateTime changedAt;
}