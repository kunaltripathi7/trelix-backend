package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.request.CreateCommentRequest;
import com.trelix.trelix_app.dto.request.UpdateCommentRequest;
import com.trelix.trelix_app.dto.response.CommentResponse;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    CommentResponse createComment(CreateCommentRequest request, UUID userId);

    List<CommentResponse> getCommentsForTask(UUID taskId, UUID userId);

    List<CommentResponse> getCommentsForMessage(UUID messageId, UUID userId);

    void deleteComment(UUID commentId, UUID userId);

    CommentResponse updateComment(UUID commentId, UpdateCommentRequest request, UUID userId);
}
