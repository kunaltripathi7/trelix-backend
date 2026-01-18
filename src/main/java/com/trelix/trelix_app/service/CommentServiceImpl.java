package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.request.CreateCommentRequest;
import com.trelix.trelix_app.dto.request.UpdateCommentRequest;
import com.trelix.trelix_app.dto.response.CommentResponse;
import com.trelix.trelix_app.entity.Comment;
import com.trelix.trelix_app.entity.Message;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.EntityType;

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
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;
    private final ChannelService channelService;
    private final WebSocketService webSocketService;

    @Override
    public CommentResponse createComment(CreateCommentRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        EntityType entityType = null;
        UUID entityId = null;

        if (request.getTaskId() != null) {
            entityType = EntityType.TASK;
            entityId = request.getTaskId();
            authorizationService.verifyTaskReadAccess(entityId, userId);
        } else {
            entityType = EntityType.MESSAGE;
            entityId = request.getMessageId();
            final UUID messageId = entityId;
            Message message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
            if (message.getChannel() != null) {
                channelService.verifyChannelAccess(message.getChannel().getId(), userId);
            }
        }

        Comment comment = Comment.builder()
                .entityType(entityType)
                .entityId(entityId)
                .user(user)
                .content(request.getContent())
                .build();

        Comment saved = commentRepository.save(comment);

        broadcastCommentEvent(saved, "COMMENT_ADDED");
        return CommentResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsForTask(UUID taskId, UUID userId) {
        authorizationService.verifyTaskReadAccess(taskId, userId);
        List<Comment> comments = commentRepository.findByEntityTypeAndEntityIdOrderByCreatedAtAsc(EntityType.TASK,
                taskId);
        return comments.stream().map(CommentResponse::from).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsForMessage(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        if (message.getChannel() != null) {
            channelService.verifyChannelAccess(message.getChannel().getId(), userId);
        }
        List<Comment> comments = commentRepository.findByEntityTypeAndEntityIdOrderByCreatedAtAsc(EntityType.MESSAGE,
                messageId);
        return comments.stream().map(CommentResponse::from).collect(Collectors.toList());
    }

    @Override
    public void deleteComment(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        boolean isOwner = comment.getUser().getId().equals(userId);

        if (!isOwner) {
            // checking admin access based on entity type
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
                // checking if user is admin/owner of the channel
                Message message = messageRepository.findById(comment.getEntityId())
                        .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
                if (message.getChannel() != null) {
                    try {
                        channelService.verifyChannelAdmin(message.getChannel().getId(), userId);
                    } catch (AccessDeniedException e) {
                        throw new AccessDeniedException("You do not have permission to delete this comment.");
                    }
                } else {
                    throw new AccessDeniedException("You do not have permission to delete this comment.");
                }
            }
        }

        commentRepository.delete(comment);
        broadcastCommentEvent(comment, "COMMENT_DELETED");
    }

    @Override
    public CommentResponse updateComment(UUID commentId, UpdateCommentRequest request, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to edit this comment.");
        }

        comment.setContent(request.getContent());
        Comment updatedComment = commentRepository.save(comment);

        broadcastCommentEvent(updatedComment, "COMMENT_UPDATED");

        return CommentResponse.from(updatedComment);
    }

    private void broadcastCommentEvent(Comment comment, String eventType) {
        if (comment.getEntityType() == EntityType.MESSAGE) {
            messageRepository.findById(comment.getEntityId()).ifPresent(message -> {
                if (message.getChannel() != null) {
                    webSocketService.broadcastEvent(
                            message.getChannel().getId(),
                            eventType,
                            CommentResponse.from(comment));
                }
            });
        }
    }
}
