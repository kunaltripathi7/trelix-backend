package com.trelix.trelix_app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id @GeneratedValue
    private UUID id;

    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne @JoinColumn(name = "task_id")
    private Task task;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


