package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Comment;
import com.trelix.trelix_app.enums.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByEntityTypeAndEntityIdOrderByCreatedAtAsc(EntityType entityType, UUID entityId);
}




