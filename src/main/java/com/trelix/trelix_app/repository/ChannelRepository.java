package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    List<Channel> findByProjectId(UUID projectId);

    List<Channel> findByTeamIdAndProjectIdIsNull(UUID teamId);

    @Query("SELECT c FROM Channel c JOIN ChannelMember cm ON c.id = cm.id.channelId " +
            "WHERE cm.id.userId = :userId AND c.teamId IS NULL AND c.projectId IS NULL")
    List<Channel> findAdHocChannelsByUserId(@Param("userId") UUID userId);

    @Query("SELECT c FROM Channel c " +
            "WHERE c.teamId IS NULL AND c.projectId IS NULL " +
            "AND EXISTS (SELECT 1 FROM ChannelMember cm1 WHERE cm1.id.channelId = c.id AND cm1.id.userId = :userId1) " +
            "AND EXISTS (SELECT 1 FROM ChannelMember cm2 WHERE cm2.id.channelId = c.id AND cm2.id.userId = :userId2) " +
            "AND (SELECT COUNT(cm) FROM ChannelMember cm WHERE cm.id.channelId = c.id) = 2")
    Channel findExistingDmBetweenUsers(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);

    boolean existsByNameAndTeamId(String name, UUID teamId);

    boolean existsByNameAndProjectId(String name, UUID projectId);
}
