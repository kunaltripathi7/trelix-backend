package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    List<Channel> findByTeamId(UUID teamId);

    List<Channel> findByProjectId(UUID projectId);

    Optional<Channel> findByTeamIdAndName(UUID teamId, String name);
}
