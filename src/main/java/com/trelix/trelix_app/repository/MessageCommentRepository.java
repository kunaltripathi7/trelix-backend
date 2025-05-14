package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.MessageComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface MessageCommentRepository extends JpaRepository<MessageComment, UUID> {
    List<MessageComment> findByMessageIdOrderByCreatedAtAsc(UUID messageId);

    List<MessageComment> findByUserId(UUID userID);
}
