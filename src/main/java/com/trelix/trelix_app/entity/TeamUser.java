package com.trelix.trelix_app.entity;

import com.trelix.trelix_app.enums.TeamRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "team_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamUser {

    @EmbeddedId
    private TeamUserId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamId")
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamRole role;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class TeamUserId implements Serializable {
        @Column(name = "user_id")
        private UUID userId;
        @Column(name = "team_id")
        private UUID teamId;
    }
}
