package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    List<Attachment> findByTaskId(UUID taskId);


    List<Attachment> findByMessageId(UUID messageId);


    List<Attachment> findByUploadedById(UUID userId);
}
