package com.trelix.trelix_app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "channels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Channel {
    @Id @GeneratedValue
    private UUID id;

    @ManyToOne @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne @JoinColumn(name = "project_id")
    private Project project;

    private String name;
    private Boolean isPrivate;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();
}
