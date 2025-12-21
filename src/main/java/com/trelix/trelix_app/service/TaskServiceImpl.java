package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.*;
import com.trelix.trelix_app.entity.Project;
import com.trelix.trelix_app.entity.Task;
import com.trelix.trelix_app.entity.Team;
import com.trelix.trelix_app.entity.TaskMember;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.enums.TaskPriority;
import com.trelix.trelix_app.enums.TaskRole;
import com.trelix.trelix_app.enums.TaskStatus;
import com.trelix.trelix_app.exception.ConflictException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ProjectRepository;
import com.trelix.trelix_app.repository.TaskMemberRepository;
import com.trelix.trelix_app.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMemberRepository taskMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final TeamService teamService;
    private final AuthorizationService authorizationService;

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
        }
        else {
            team = authorizationService.verifyTeamMembership(request.teamId(), creatorId).getTeam();
            teamId = request.teamId();
        }

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
    public PagedTaskResponse getTasks(UUID teamId, UUID projectId, TaskStatus status, TaskPriority priority, int page, int size, UUID requesterId, String query) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Task> taskPage = taskRepository.findTasksForUser(requesterId, teamId, projectId, status, priority, query, pageable);

        //can't filter out here -> as it breaks the pagination and also very inefficient (non scalable)

        return PagedTaskResponse.from(taskPage);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDetailResponse getTaskById(UUID taskId, UUID requesterId) {
        Task task = taskRepository.findTaskDetailById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        authorizationService.verifyTaskAccess(task, requesterId);

        return TaskDetailResponse.from(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(UUID taskId, UpdateTaskRequest request, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        authorizationService.verifyTaskAccess(task, requesterId);

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
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        authorizationService.verifyTaskAccess(task, requesterId);

        task.setStatus(newStatus);

        Task updatedTask = taskRepository.save(task);

        return TaskResponse.from(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(UUID taskId, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        authorizationService.verifyTaskAccess(task, requesterId);

        taskRepository.delete(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskMemberResponse> getTaskMembers(UUID taskId, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        authorizationService.verifyTaskAccess(task, requesterId);

        List<TaskMember> taskMembers = taskMemberRepository.findByIdTaskId(taskId);

        return taskMembers.stream()
                .map(TaskMemberResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskMemberResponse assignMember(UUID taskId, AssignTaskMemberRequest request, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        authorizationService.verifyTaskAccess(task, requesterId);

        User userToAssign = userService.findById(request.userId());

        authorizationService.verifyTeamMembership(task.getTeamId(), request.userId());
        if (task.getProjectId() != null) {
            authorizationService.verifyProjectMembership(task.getProjectId(), request.userId());
        }

        if (taskMemberRepository.existsByIdTaskIdAndIdUserId(taskId, request.userId())) {
            throw new ConflictException("User " + request.userId() + " is already assigned to task " + taskId, ErrorCode.INVALID_INPUT);
        }

        TaskMember taskMember = TaskMember.builder()
                .id(new TaskMember.TaskMemberId(taskId, request.userId()))
                .task(task)
                .user(userToAssign)
                .role(request.role())
                .build();

        TaskMember savedTaskMember = taskMemberRepository.save(taskMember);

        return TaskMemberResponse.from(savedTaskMember);
    }

    @Override
    @Transactional
    public TaskMemberResponse updateMemberRole(UUID taskId, UUID userId, TaskRole newRole, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        authorizationService.verifyTaskAdmin(taskId, requesterId);

        TaskMember taskMember = taskMemberRepository.findByIdTaskIdAndIdUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " is not a member of task " + taskId));

        taskMember.setRole(newRole);

        TaskMember updatedTaskMember = taskMemberRepository.save(taskMember);

        return TaskMemberResponse.from(updatedTaskMember);
    }

    @Override
    @Transactional
    public void removeMember(UUID taskId, UUID userId, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        authorizationService.verifyTaskMembership(taskId, requesterId);

        TaskMember taskMember = taskMemberRepository.findByIdTaskIdAndIdUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " is not a member of task " + taskId));

        taskMemberRepository.delete(taskMember);
    }
}
