package com.trelix.trelix_app.entity;

import com.trelix.trelix_app.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private UUID id;

    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 500)
    private String refreshToken;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TeamUser> teamUsers = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL)
    private List<Attachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "notifier", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Event> events = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TaskComment> taskComments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<MessageComment> messageComments = new ArrayList<>();

    @OneToMany(mappedBy = "changedBy", cascade = CascadeType.ALL)
    private List<TaskStatusChange> statusChanges = new ArrayList<>();
}
