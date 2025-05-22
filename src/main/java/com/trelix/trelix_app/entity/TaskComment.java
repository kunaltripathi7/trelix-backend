package com.trelix.trelix_app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task_comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskComment {
    @Id @GeneratedValue
    private UUID id;

    @ManyToOne @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
