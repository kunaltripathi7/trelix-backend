package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Attachment;
import com.trelix.trelix_app.enums.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    List<Attachment> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(EntityType entityType, UUID entityId);

    long countByEntityTypeAndEntityId(EntityType entityType, UUID entityId);

    boolean existsByIdAndEntityTypeAndEntityId(UUID id, EntityType entityType, UUID entityId);

    List<Attachment> findByUploadedBy(UUID userId);
}
