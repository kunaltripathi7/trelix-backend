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
    
    public TaskCommentDTO createComment(UUID taskId, TaskCommentDTO commentDTO, UUID userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        TaskComment comment = TaskComment.builder()
                .task(task)
                .user(user)
                .content(commentDTO.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return AppMapper.convertToCommentDTO(taskCommentRepository.save(comment));
    }

    public List<TaskCommentDTO> getComments(UUID taskId) {
        List<TaskComment> comments = taskCommentRepository.findByTaskId(taskId);
        return comments.stream()
                .map(AppMapper::convertToCommentDTO)
                .collect(Collectors.toList());
    }

    public TaskCommentDTO getComment(UUID taskId, UUID commentId) {
        TaskComment comment = taskCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        if (!comment.getTask().getId().equals(taskId)) {
            throw new ResourceNotFoundException("Comment does not belong to the specified task");
        }
        return AppMapper.convertToCommentDTO(comment);
    }

    public TaskCommentDTO updateComment(UUID taskId, UUID commentId, TaskCommentDTO commentDTO, UUID id) {
        TaskComment comment = taskCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        if (!comment.getTask().getId().equals(taskId)) {
            throw new ResourceNotFoundException("Comment does not belong to the specified task");
        }
        if (!comment.getUser().getId().equals(id)) {
            throw new ResourceNotFoundException("User does not have permission to update this comment");
        }
        comment.setContent(commentDTO.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        return AppMapper.convertToCommentDTO(taskCommentRepository.save(comment));
    }

    public void deleteComment(UUID taskId, UUID commentId, UUID id) {
        TaskComment comment = taskCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        if (!comment.getTask().getId().equals(taskId)) {
            throw new ResourceNotFoundException("Comment does not belong to the specified task");
        }
        if (!comment.getUser().getId().equals(id)) {
            throw new ResourceNotFoundException("User does not have permission to delete this comment");
        }
        taskCommentRepository.delete(comment);
    }
}
