package com.trelix.trelix_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "task_members")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskMember {

    @Id @GeneratedValue
    private UUID id;

    @ManyToOne @JoinColumn(name = "task_id", referencedColumnName = "id", nullable = false)
    private Task task;

    @ManyToOne @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;
}
