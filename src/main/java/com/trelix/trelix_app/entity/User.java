package com.trelix.trelix_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id @GeneratedValue
    @Column(name = "user_id")
    private UUID id;

    private String name;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;
    private boolean enabled = true;


    @Column(name = "authority")
    private String role;

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.toUpperCase()));
    }

    @Override
    public String getUsername() {
        return this.username;  // Using email as the username
    }

    // These are security flags - set to true for now
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
