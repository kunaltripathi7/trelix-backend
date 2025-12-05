package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.TaskDTO;
import com.trelix.trelix_app.dto.TaskDetailsDTO;
import com.trelix.trelix_app.dto.TaskRequest;
import com.trelix.trelix_app.dto.TaskSearchCriteria;
import com.trelix.trelix_app.entity.Project;
import com.trelix.trelix_app.entity.Task;
import com.trelix.trelix_app.entity.TaskStatusChange;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.TaskPriority;
import com.trelix.trelix_app.enums.TaskStatus;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ProjectRepository;
import com.trelix.trelix_app.repository.TaskRepository;
import com.trelix.trelix_app.repository.TaskStatusChangeRepository;
import com.trelix.trelix_app.repository.UserRepository;
import com.trelix.trelix_app.util.AppMapper;
import com.trelix.trelix_app.util.TaskSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskStatusChangeRepository taskStatusChangeRepository;
    private final AuthorizationService authService;

    private LocalDateTime parseDateTimeWithDefaultTime(String input) {
        try {
            return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd[ HH:mm]"));
        } catch (DateTimeParseException e) {
            return LocalDate.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(9, 0);
        }
    }

    public TaskDTO createTask(TaskRequest request, UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        authService.checkProjectAccess(project.getTeam().getId(), projectId, userId);

        User assignedUser = null;
        if (request.getAssignedTo() != null) {
            assignedUser = userRepository.findByEmail(request.getAssignedTo())
                    .orElseThrow(() -> new ResourceNotFoundException("User to assign not found with email: " + request.getAssignedTo()));
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(TaskStatus.valueOf(request.getStatus()))
                .priority(TaskPriority.valueOf(request.getPriority()))
                .dueDate(parseDateTimeWithDefaultTime(request.getDueDate()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .project(project)
                .assignedTo(assignedUser)
                .build();
        return AppMapper.convertToTaskDTO(taskRepository.save(task));
    }

    public List<TaskDTO> getTasksByProjectId(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        authService.checkProjectAccess(project.getTeam().getId(), projectId, userId);
        List<Task> tasks = taskRepository.findByProjectId(project.getId());
        return tasks.stream().map(AppMapper::convertToTaskDTO).toList();
    }

    public TaskDetailsDTO getTaskById(UUID taskId, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        authService.checkTaskAccessByTaskId(taskId, userId);
        return AppMapper.convertToTaskDetailsDTO(task);
    }

    public TaskDTO updateTask(UUID taskId, TaskRequest taskRequest, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        authService.checkTaskAccessByTaskId(taskId, userId);

        if (!task.getStatus().toString().equals(taskRequest.getStatus())) {
            User changedByUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            TaskStatusChange statusChange = TaskStatusChange.builder()
                    .task(task)
                    .previousStatus(task.getStatus())
                    .newStatus(TaskStatus.valueOf(taskRequest.getStatus()))
                    .changedAt(LocalDateTime.now())
                    .changedBy(changedByUser)
                    .build();
            taskStatusChangeRepository.save(statusChange);
        }

        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(TaskStatus.valueOf(taskRequest.getStatus()));
        task.setPriority(TaskPriority.valueOf(taskRequest.getPriority()));
        task.setDueDate(parseDateTimeWithDefaultTime(taskRequest.getDueDate()));
        task.setUpdatedAt(LocalDateTime.now());
        return AppMapper.convertToTaskDTO(taskRepository.save(task));
    }

    public void deleteTask(UUID taskId, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        authService.checkTaskAccessByTaskId(taskId, userId);
        taskRepository.delete(task);
    }

    public List<TaskDTO> searchTasks(UUID projectId, TaskSearchCriteria criteria, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        authService.checkProjectAccess(project.getTeam().getId(), projectId, userId);
        criteria.setProjectId(projectId);
        return taskRepository.findAll(TaskSpecification.byCriteria(criteria))
                .stream().map(AppMapper::convertToTaskDTO).toList();
    }
}
