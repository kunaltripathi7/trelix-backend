package com.trelix.trelix_app.entity;

import com.trelix.trelix_app.enums.ROLE;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user")
    private List<TeamUser> teamMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ProjectMember> projectMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<TaskMember> taskAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ChannelMember> channelMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    private List<Message> sentMessages = new ArrayList<>();

    @OneToMany(mappedBy = "uploadedBy")
    private List<Attachment> uploadedAttachments = new ArrayList<>();

    @OneToMany(mappedBy = "notifierId")
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "actorId")
    private List<Notification> actedOnNotifications = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy")
    private List<Event> createdEvents = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "changedBy")
    private List<TaskStatusChange> taskStatusChanges = new ArrayList<>();

    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    private ROLE role;
}
