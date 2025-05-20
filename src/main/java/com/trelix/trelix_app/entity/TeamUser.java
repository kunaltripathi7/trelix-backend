package com.trelix.trelix_app.entity;

import com.trelix.trelix_app.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "team_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamUser {
    @Id @GeneratedValue
    private UUID id;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime joinedAt;
}
