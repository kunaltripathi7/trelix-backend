package com.trelix.trelix_app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {
    @Id @GeneratedValue
    private UUID id;

    private String fileName;
    private String fileType;
    private Long fileSize;
    private String url;

    @ManyToOne @JoinColumn(name = "uploaded_by", referencedColumnName = "user_id", nullable = false)
    private User uploadedBy;

    @ManyToOne @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne @JoinColumn(name = "message_id")
    private Message message;

    private LocalDateTime createdAt;
}