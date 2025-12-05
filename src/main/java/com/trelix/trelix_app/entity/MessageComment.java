package com.trelix.trelix_app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "message_comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageComment {
    @Id @GeneratedValue
    private UUID id;

    @ManyToOne @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
