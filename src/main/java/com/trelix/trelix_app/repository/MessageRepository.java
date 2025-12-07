package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    // Channel-specific methods (existing)
    Page<Message> findByChannelIdOrderByCreatedAtAsc(UUID channelId, Pageable pageable);
    long countByChannelId(UUID channelId);
    @Query("SELECT m FROM Message m WHERE m.id = :messageId AND m.channelId = :channelId")
    Optional<Message> findByIdAndChannelId(@Param("messageId") UUID messageId,
                                            @Param("channelId") UUID channelId);

    // Direct Message-specific methods (new)
    Page<Message> findByDirectMessageIdOrderByCreatedAtAsc(UUID directMessageId, Pageable pageable);
    long countByDirectMessageId(UUID directMessageId);
    @Query("SELECT m FROM Message m WHERE m.id = :messageId AND m.directMessageId = :dmId")
    Optional<Message> findByIdAndDirectMessageId(@Param("messageId") UUID messageId,
                                                  @Param("dmId") UUID dmId);
}
