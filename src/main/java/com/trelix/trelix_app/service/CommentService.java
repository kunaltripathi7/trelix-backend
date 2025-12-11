//package com.trelix.trelix_app.service;
//
//import com.trelix.trelix_app.dto.CommentDTO;
//import com.trelix.trelix_app.entity.Comment;
//import com.trelix.trelix_app.entity.Message;
//import com.trelix.trelix_app.entity.Task;
//import com.trelix.trelix_app.entity.User;
//import com.trelix.trelix_app.exception.InvalidRequestException;
//import com.trelix.trelix_app.exception.ResourceNotFoundException;
//import com.trelix.trelix_app.repository.CommentRepository;
//import com.trelix.trelix_app.repository.MessageRepository;
//import com.trelix.trelix_app.repository.TaskRepository;
//import com.trelix.trelix_app.repository.UserRepository;
//import com.trelix.trelix_app.util.AppMapper;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class CommentService {
//
//    private final CommentRepository commentRepository;
//    private final TaskRepository taskRepository;
//    private final MessageRepository messageRepository;
//    private final UserRepository userRepository;
//    private final AuthorizationService authService;
//
//    public CommentDTO createComment(CommentDTO commentDTO, UUID userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
//
//        Task task = null;
//        Message message = null;
//
//        if (commentDTO.getTaskId() != null) {
//            task = taskRepository.findById(commentDTO.getTaskId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + commentDTO.getTaskId()));
//            authService.checkTaskAccessByTaskId(task.getId(), userId);
//        } else if (commentDTO.getMessageId() != null) {
//            message = messageRepository.findById(commentDTO.getMessageId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + commentDTO.getMessageId()));
//            authService.checkChannelAccess(message.getChannel(), userId);
//        } else {
//            throw new InvalidRequestException("A comment must be associated with either a task or a message.", com.trelix.trelix_app.enums.ErrorCode.INVALID_INPUT);
//        }
//
//        Comment comment = Comment.builder()
//                .task(task)
//                .message(message)
//                .user(user)
//                .content(commentDTO.getContent())
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        return AppMapper.convertToCommentDTO(commentRepository.save(comment));
//    }
//
//    public List<CommentDTO> getCommentsForTask(UUID taskId, UUID userId) {
//        authService.checkTaskAccessByTaskId(taskId, userId);
//        List<Comment> comments = commentRepository.findByTaskId(taskId);
//        return comments.stream().map(AppMapper::convertToCommentDTO).collect(Collectors.toList());
//    }
//
//    public List<CommentDTO> getCommentsForMessage(UUID messageId, UUID userId) {
//        Message message = messageRepository.findById(messageId)
//                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
//        authService.checkChannelAccess(message.getChannel(), userId);
//        List<Comment> comments = commentRepository.findByMessageId(messageId);
//        return comments.stream().map(AppMapper::convertToCommentDTO).collect(Collectors.toList());
//    }
//
//    public void deleteComment(UUID commentId, UUID userId) {
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
//
//        boolean isOwner = comment.getUser().getId().equals(userId);
//        boolean isAdmin = false;
//
//        if (comment.getTask() != null) {
//            isAdmin = authService.checkIfUserIsAdminInProject(comment.getTask().getProject().getId(), userId);
//        } else if (comment.getMessage() != null) {
//            isAdmin = authService.checkIfUserIsAdminInTeam(comment.getMessage().getChannel().getTeam().getId(), userId);
//        }
//
//        if (!isOwner && !isAdmin) {
//            throw new AccessDeniedException("You do not have permission to delete this comment.");
//        }
//
//        commentRepository.delete(comment);
//    }
//}
