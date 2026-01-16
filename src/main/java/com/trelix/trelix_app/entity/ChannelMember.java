package com.trelix.trelix_app.entity;

import com.trelix.trelix_app.enums.ChannelRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "channel_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelMember {

    @EmbeddedId
    private ChannelMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("channelId")
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelRole role;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class ChannelMemberId implements Serializable {
        @Column(name = "channel_id")
        private UUID channelId;
        @Column(name = "user_id")
        private UUID userId;
    }
}




