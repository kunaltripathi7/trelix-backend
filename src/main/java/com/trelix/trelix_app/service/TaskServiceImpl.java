package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.request.*;
import com.trelix.trelix_app.dto.response.*;
import com.trelix.trelix_app.dto.common.*;
import com.trelix.trelix_app.entity.Project;
import com.trelix.trelix_app.entity.Task;
import com.trelix.trelix_app.entity.Team;
import com.trelix.trelix_app.entity.TaskMember;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.enums.NotificationType;
import com.trelix.trelix_app.enums.TaskPriority;
import com.trelix.trelix_app.enums.TaskRole;
import com.trelix.trelix_app.enums.TaskStatus;
import com.trelix.trelix_app.exception.ConflictException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.TaskMemberRepository;
import com.trelix.trelix_app.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMemberRepository taskMemberRepository;
    private final UserService userService;
    private final AuthorizationService authorizationService;
    private final KafkaProducerService kafkaProducerService;

    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request, UUID creatorId) {
        Project project = null;
        Team team = null;
        UUID projectId = null;
        UUID teamId = null;
        if (request.projectId() != null) {
            project = authorizationService.verifyProjectMembership(request.projectId(), creatorId).getProject();
            projectId = request.projectId();
            team = project.getTeam();
            teamId = team.getId();
        } else if (request.teamId() != null) {
            team = authorizationService.verifyTeamMembership(request.teamId(), creatorId).getTeam();
            teamId = request.teamId();
        } else
            throw new IllegalArgumentException("Task must belong to either a Project or a Team");

        TaskStatus status = Optional.ofNullable(request.status()).orElse(TaskStatus.TODO);
        TaskPriority priority = Optional.ofNullable(request.priority()).orElse(TaskPriority.MEDIUM);

        Task task = Task.builder()
                .team(team)
                .teamId(teamId)
                .project(project)
                .projectId(projectId)
                .title(request.title())
                .description(request.description())
                .status(status)
                .priority(priority)
                .dueDate(request.dueDate())
                .build();

        Task savedTask = taskRepository.save(task);

        return TaskResponse.from(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedTaskResponse getTasks(UUID teamId, UUID projectId, TaskStatus status, TaskPriority priority, int page,
            int size, UUID requesterId, String query) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Task> taskPage = taskRepository.findTasksForUser(requesterId, teamId, projectId, status, priority, query,
                pageable);

        // can't filter out here -> as it breaks the pagination and also very
        // inefficient (non scalable)

        return PagedTaskResponse.from(taskPage);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDetailResponse getTaskById(UUID taskId, UUID requesterId) {
        Task task = taskRepository.findTaskDetailById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        authorizationService.verifyTaskReadAccess(task, requesterId);

        return TaskDetailResponse.from(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(UUID taskId, UpdateTaskRequest request, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        authorizationService.verifyTaskWriteAccess(task, requesterId);

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());

        Task updatedTask = taskRepository.save(task);

        return TaskResponse.from(updatedTask);
    }

    @Override
    @Transactional
    public TaskResponse updateTaskStatus(UUID taskId, TaskStatus newStatus, UUID requesterId) {
        Task task = taskRepository.findTaskMembersById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        authorizationService.verifyTaskWriteAccess(task, requesterId);

        task.setStatus(newStatus);

        Task updatedTask = taskRepository.save(task);

        // notify all task members about the status change (except the one who made the
        // change)
        task.getMembers().forEach(member -> {
            if (!member.getUser().getId().equals(requesterId)) {
                kafkaProducerService.sendNotification(new NotificationEvent(
                        member.getUser().getId(),
                        requesterId,
                        NotificationType.TASK_STATUS_CHANGED,
                        "Task Status Changed",
                        "Task '" + task.getTitle() + "' status changed to: " + newStatus,
                        taskId,
                        Map.of("taskTitle", task.getTitle(), "oldStatus", task.getStatus().toString(), "newStatus",
                                newStatus.toString())));
            }
        });

        return TaskResponse.from(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(UUID taskId, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        authorizationService.verifyTaskWriteAccess(task, requesterId);

        taskRepository.delete(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskMemberResponse> getTaskMembers(UUID taskId, UUID requesterId) {
        Task task = taskRepository.findTaskMembersById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        authorizationService.verifyTaskReadAccess(task, requesterId);
        return TaskMemberResponse.from(task.getMembers());
    }

    @Override
    @Transactional
    public TaskMemberResponse assignMember(UUID taskId, AssignTaskMemberRequest request, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        authorizationService.verifyTaskReadAccess(task, requesterId);

        if (!request.userId().equals(requesterId)) {
            authorizationService.verifyTaskWriteAccess(task, requesterId);
        }

        User userToAssign = userService.findById(request.userId());

        if (task.getProjectId() != null) {
            authorizationService.verifyProjectMembership(task.getProjectId(), request.userId());
        } else {
            authorizationService.verifyTeamMembership(task.getTeamId(), request.userId());
        }

        if (taskMemberRepository.existsByIdTaskIdAndIdUserId(taskId, request.userId())) {
            throw new ConflictException("User " + request.userId() + " is already assigned to task " + taskId,
                    ErrorCode.INVALID_INPUT);
        }

        TaskMember taskMember = TaskMember.builder()
                .id(new TaskMember.TaskMemberId(taskId, request.userId()))
                .task(task)
                .user(userToAssign)
                .role(request.role())
                .build();

        // race condition to tackle when two threads are trying to insert same (taskId,
        // userId) -> DB will throw constraint error
        try {
            TaskMember savedTaskMember = taskMemberRepository.save(taskMember);
            kafkaProducerService.sendNotification(new NotificationEvent(
                    request.userId(),
                    requesterId,
                    NotificationType.TASK_ASSIGNED,
                    "Task Assigned",
                    "You have been assigned to the task: " + task.getTitle(),
                    taskId,
                    Map.of("taskTitle", task.getTitle())));
            return TaskMemberResponse.from(savedTaskMember);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("User " + request.userId() + " is already assigned to task " + taskId,
                    ErrorCode.INVALID_INPUT);
        }
    }

    @Override
    @Transactional
    public TaskMemberResponse updateMemberRole(UUID taskId, UUID userId, TaskRole newRole, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        authorizationService.verifyTaskWriteAccess(task, requesterId);

        TaskMember taskMember = taskMemberRepository.findByIdTaskIdAndIdUserId(taskId, userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User " + userId + " is not a member of task " + taskId));

        taskMember.setRole(newRole);

        TaskMember updatedTaskMember = taskMemberRepository.save(taskMember);

        return TaskMemberResponse.from(updatedTaskMember);
    }

    @Override
    @Transactional
    public void removeMember(UUID taskId, UUID userId, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        if (userId.equals(requesterId)) {
            authorizationService.verifyTaskMembership(taskId, requesterId);
        } else {
            authorizationService.verifyTaskWriteAccess(task, requesterId);
        }

        TaskMember taskMember = taskMemberRepository.findByIdTaskIdAndIdUserId(taskId, userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User " + userId + " is not a member of task " + taskId));

        taskMemberRepository.delete(taskMember);
    }
}
