package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.TaskCommentDTO;
import com.trelix.trelix_app.entity.Task;
import com.trelix.trelix_app.entity.TaskComment;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.TaskCommentRepository;
import com.trelix.trelix_app.repository.TaskRepository;
import com.trelix.trelix_app.repository.UserRepository;
import com.trelix.trelix_app.util.AppMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskCommentService {

    private final TaskCommentRepository taskCommentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authService;

    public TaskCommentDTO createComment(UUID taskId, TaskCommentDTO commentDTO, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        authService.checkTaskAccessByTaskId(taskId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        TaskComment comment = TaskComment.builder()
                .task(task)
                .user(user)
                .content(commentDTO.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return AppMapper.convertToCommentDTO(taskCommentRepository.save(comment));
    }

    public List<TaskCommentDTO> getComments(UUID taskId, UUID userId) {
        authService.checkTaskAccessByTaskId(taskId, userId);
        List<TaskComment> comments = taskCommentRepository.findByTaskId(taskId);
        return comments.stream()
                .map(AppMapper::convertToCommentDTO)
                .collect(Collectors.toList());
    }

    public TaskCommentDTO getComment(UUID commentId, UUID userId) {
        TaskComment comment = taskCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        authService.checkTaskAccessByTaskId(comment.getTask().getId(), userId);
        return AppMapper.convertToCommentDTO(comment);
    }

    public TaskCommentDTO updateComment(UUID commentId, TaskCommentDTO commentDTO, UUID userId) {
        TaskComment comment = taskCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to update this comment.");
        }
        authService.checkTaskAccessByTaskId(comment.getTask().getId(), userId);
        comment.setContent(commentDTO.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        return AppMapper.convertToCommentDTO(taskCommentRepository.save(comment));
    }

    public void deleteComment(UUID commentId, UUID userId) {
        TaskComment comment = taskCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        authService.checkTaskAccessByTaskId(comment.getTask().getId(), userId);
        if (!comment.getUser().getId().equals(userId) && !authService.checkIfUserIsAdminInProject(comment.getTask().getProject().getId(), userId)) {
            throw new AccessDeniedException("You do not have permission to delete this comment.");
        }
        taskCommentRepository.delete(comment);
    }
}
