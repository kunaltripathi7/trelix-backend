package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Event;
import com.trelix.trelix_app.enums.EventEntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

        @Query("SELECT e FROM Event e WHERE " +
                        "(:entityType IS NULL OR e.entityType = :entityType) AND " +
                        "(:entityId IS NULL OR e.entityId = :entityId) AND " +
                        "(:start IS NULL OR e.startTime >= :start) AND " +
                        "(:end IS NULL OR e.endTime <= :end) " +
                        "ORDER BY e.startTime ASC")
        Page<Event> findByFilters(
                        @Param("entityType") EventEntityType entityType,
                        @Param("entityId") UUID entityId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        Pageable pageable);
}
