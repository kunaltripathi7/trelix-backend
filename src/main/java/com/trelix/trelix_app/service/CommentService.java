package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.CommentDTO;
import java.util.List;
import java.util.UUID;

public interface CommentService {
    CommentDTO createComment(CommentDTO commentDTO, UUID userId);

    List<CommentDTO> getCommentsForTask(UUID taskId, UUID userId);

    List<CommentDTO> getCommentsForMessage(UUID messageId, UUID userId);

    void deleteComment(UUID commentId, UUID userId);
}
