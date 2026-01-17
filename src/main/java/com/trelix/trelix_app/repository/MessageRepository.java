package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("SELECT m FROM Message m WHERE m.channel.id = :channelId ORDER BY m.createdAt ASC")
    Page<Message> findByChannelIdOrderByCreatedAtAsc(@Param("channelId") UUID channelId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.channel.id = :channelId")
    long countByChannelId(@Param("channelId") UUID channelId);
}
