//package com.trelix.trelix_app.repository;
//
//import com.trelix.trelix_app.entity.Event;
//import com.trelix.trelix_app.enums.EventEntityType;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//public interface EventRepository extends JpaRepository<Event, UUID> {
//
//    Page<Event> findByEntityTypeAndEntityIdOrderByStartTimeAsc(
//            EventEntityType entityType,
//            UUID entityId,
//            Pageable pageable
//    );
//
//    Page<Event> findByEntityTypeOrderByStartTimeAsc(
//            EventEntityType entityType,
//            Pageable pageable
//    );
//
//    @Query("SELECT e FROM Event e WHERE e.startTime >= :start AND e.endTime <= :end ORDER BY e.startTime ASC")
//    Page<Event> findByDateRange(
//            @Param("start") LocalDateTime start,
//            @Param("end") LocalDateTime end,
//            Pageable pageable
//    );
//
//    @Query("SELECT e FROM Event e WHERE e.entityType = :entityType AND e.entityId = :entityId " +
//            "AND e.startTime >= :start AND e.endTime <= :end ORDER BY e.startTime ASC")
//    Page<Event> findByEntityAndDateRange(
//            @Param("entityType") EventEntityType entityType,
//            @Param("entityId") UUID entityId,
//            @Param("start") LocalDateTime start,
//            @Param("end") LocalDateTime end,
//            Pageable pageable
//    );
//
//    @Query("SELECT e FROM Event e WHERE " +
//            "(:entityType IS NULL OR e.entityType = :entityType) AND " +
//            "(:entityId IS NULL OR e.entityId = :entityId) AND " +
//            "(:start IS NULL OR e.startTime >= :start) AND " +
//            "(:end IS NULL OR e.endTime <= :end) " +
//            "ORDER BY e.startTime ASC")
//    Page<Event> findByFilters(
//            @Param("entityType") EventEntityType entityType,
//            @Param("entityId") UUID entityId,
//            @Param("start") LocalDateTime start,
//            @Param("end") LocalDateTime end,
//            Pageable pageable
//    );
//
//    List<Event> findByCreatedByOrderByStartTimeAsc(UUID userId);
//
//    long countByEntityTypeAndEntityId(EventEntityType entityType, UUID entityId);
//
//    @Query("SELECT e FROM Event e WHERE e.entityType = :entityType AND e.entityId = :entityId " +
//            "AND e.startTime > :now ORDER BY e.startTime ASC")
//    List<Event> findUpcomingEvents(
//            @Param("entityType") EventEntityType entityType,
//            @Param("entityId") UUID entityId,
//            @Param("now") LocalDateTime now
//    );
//}
