package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    List<Channel> findByTeamId(UUID teamId);

    List<Channel> findByProjectId(UUID projectId);

    List<Channel> findByTeamIdAndProjectIdIsNull(UUID teamId); // Team channels only

    Optional<Channel> findByIdAndTeamId(UUID channelId, UUID teamId);

    @Query("SELECT c FROM Channel c JOIN ChannelMember cm ON c.id = cm.id.channelId " +
           "WHERE cm.id.userId = :userId AND c.teamId IS NULL AND c.projectId IS NULL")
    List<Channel> findAdHocChannelsByUserId(@Param("userId") UUID userId);
}
