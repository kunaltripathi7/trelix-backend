package com.trelix.trelix_app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id @GeneratedValue
    private UUID id;

    @ManyToOne @JoinColumn(name = "notifier_id", nullable = false)
    private User notifier;

    @ManyToOne @JoinColumn(name = "actor_id")
    private User actor;

    private String type;    // e.g. TASK_ASSIGNED, MESSAGE_MENTION
    private Boolean isRead;

    @ManyToOne @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne @JoinColumn(name = "task_id")
    private Task task;

    private LocalDateTime createdAt;
}