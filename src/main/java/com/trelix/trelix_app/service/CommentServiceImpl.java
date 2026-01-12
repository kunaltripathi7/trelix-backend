package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.CommentDTO;
import com.trelix.trelix_app.dto.UserResponse;
import com.trelix.trelix_app.entity.Comment;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.EntityType;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.exception.InvalidRequestException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.CommentRepository;
import com.trelix.trelix_app.repository.TaskRepository;
import com.trelix.trelix_app.repository.MessageRepository;
import com.trelix.trelix_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;

    @Override
    public CommentDTO createComment(CommentDTO commentDTO, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        EntityType entityType = null;
        UUID entityId = null;

        if (commentDTO.getTaskId() != null) {
            entityType = EntityType.TASK;
            entityId = commentDTO.getTaskId();
            // Verify user has access to task
            authorizationService.verifyTaskReadAccess(entityId, userId);
        } else if (commentDTO.getMessageId() != null) {
            entityType = EntityType.MESSAGE;
            entityId = commentDTO.getMessageId();
            final UUID messageId = entityId;
            // Verify user has access to message's channel
            var message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
            // Note: Channel access verification would need to be added to
            // AuthorizationService
            // For now, we'll skip this check or add a basic check
        } else {
            throw new InvalidRequestException("A comment must be associated with either a task or a message.",
                    ErrorCode.INVALID_INPUT);
        }

        Comment comment = Comment.builder()
                .entityType(entityType)
                .entityId(entityId)
                .user(user)
                .content(commentDTO.getContent())
                .build();

        Comment saved = commentRepository.save(comment);
        return toCommentDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsForTask(UUID taskId, UUID userId) {
        authorizationService.verifyTaskReadAccess(taskId, userId);
        List<Comment> comments = commentRepository.findByEntityTypeAndEntityIdOrderByCreatedAtAsc(EntityType.TASK,
                taskId);
        return comments.stream().map(this::toCommentDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsForMessage(UUID messageId, UUID userId) {
        var message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        // Note: Channel access verification would need to be added
        List<Comment> comments = commentRepository.findByEntityTypeAndEntityIdOrderByCreatedAtAsc(EntityType.MESSAGE,
                messageId);
        return comments.stream().map(this::toCommentDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteComment(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        boolean isOwner = comment.getUser().getId().equals(userId);

        if (!isOwner) {
            // Check admin access based on entity type
            if (comment.getEntityType() == EntityType.TASK) {
                try {
                    authorizationService.verifyTaskWriteAccess(
                            taskRepository.findById(comment.getEntityId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Task not found")),
                            userId);
                } catch (AccessDeniedException e) {
                    throw new AccessDeniedException("You do not have permission to delete this comment.");
                }
            } else {
                // For messages, would need channel admin check
                throw new AccessDeniedException("You do not have permission to delete this comment.");
            }
        }

        commentRepository.delete(comment);
    }

    private CommentDTO toCommentDTO(Comment comment) {
        UserResponse userResponse = new UserResponse(
                comment.getUser().getId(),
                comment.getUser().getName(),
                comment.getUser().getEmail(),
                comment.getUser().getCreatedAt());

        UUID taskId = comment.getEntityType() == EntityType.TASK ? comment.getEntityId() : null;
        UUID messageId = comment.getEntityType() == EntityType.MESSAGE ? comment.getEntityId() : null;

        return CommentDTO.builder()
                .id(comment.getId())
                .taskId(taskId)
                .messageId(messageId)
                .user(userResponse)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
