package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findByTaskId(UUID taskId);

    List<Event> findByCreatedById(UUID userId);

    List<Event> findByTeamId(UUID teamId);
    List<Event> findByProjectId(UUID projectId);

    List<Event> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
