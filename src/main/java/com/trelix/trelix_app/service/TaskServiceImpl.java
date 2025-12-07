package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.*;
import com.trelix.trelix_app.entity.Project;
import com.trelix.trelix_app.entity.Task;
import com.trelix.trelix_app.entity.TaskMember;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.TaskPriority;
import com.trelix.trelix_app.enums.TaskRole;
import com.trelix.trelix_app.enums.TaskStatus;
import com.trelix.trelix_app.exception.BadRequestException;
import com.trelix.trelix_app.exception.ConflictException;
import com.trelix.trelix_app.exception.ForbiddenException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ProjectRepository;
import com.trelix.trelix_app.repository.TaskMemberRepository;
import com.trelix.trelix_app.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMemberRepository taskMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final TeamService teamService;
    private final ProjectService projectService;
    private final TeamAuthorizationService teamAuthorizationService;
    private final ProjectAuthorizationService projectAuthorizationService;
    private final TaskAuthorizationService taskAuthorizationService;

    public TaskServiceImpl(TaskRepository taskRepository,
                           TaskMemberRepository taskMemberRepository,
                           ProjectRepository projectRepository,
                           UserService userService,
                           TeamService teamService,
                           ProjectService projectService,
                           TeamAuthorizationService teamAuthorizationService,
                           ProjectAuthorizationService projectAuthorizationService,
                           TaskAuthorizationService taskAuthorizationService) {
        this.taskRepository = taskRepository;
        this.taskMemberRepository = taskMemberRepository;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.teamService = teamService;
        this.projectService = projectService;
        this.teamAuthorizationService = teamAuthorizationService;
        this.projectAuthorizationService = projectAuthorizationService;
        this.taskAuthorizationService = taskAuthorizationService;
    }

    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request, UUID creatorId) {
        teamAuthorizationService.verifyTeamMember(request.teamId(), creatorId);

        if (request.projectId() != null) {
            Project project = projectRepository.findById(request.projectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + request.projectId()));

            if (!project.getTeamId().equals(request.teamId())) {
                throw new BadRequestException("Project " + request.projectId() + " does not belong to team " + request.teamId());
            }
            projectAuthorizationService.verifyProjectMember(request.projectId(), creatorId);
        }

        TaskStatus status = Optional.ofNullable(request.status()).orElse(TaskStatus.TODO);
        TaskPriority priority = Optional.ofNullable(request.priority()).orElse(TaskPriority.MEDIUM);

        Task task = Task.builder()
                .teamId(request.teamId())
                .projectId(request.projectId())
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
    public PagedTaskResponse getTasks(UUID teamId, UUID projectId, TaskStatus status, TaskPriority priority, int page, int size, UUID requesterId) {
        teamAuthorizationService.verifyTeamMember(teamId, requesterId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Task> taskPage = taskRepository.findByFilters(teamId, projectId, status, priority, pageable);

        return PagedTaskResponse.from(taskPage);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDetailResponse getTaskById(UUID taskId, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        teamAuthorizationService.verifyTeamMember(task.getTeamId(), requesterId);

        String projectName = null;
        if (task.getProjectId() != null) {
            projectName = projectRepository.findById(task.getProjectId())
                    .map(Project::getName)
                    .orElse(null);
        }

        String teamName = teamService.findById(task.getTeamId())
                .map(team -> team.getName())
                .orElse("Unknown Team");

        List<TaskMember> taskMembers = taskMemberRepository.findByIdTaskId(taskId);

        return TaskDetailResponse.from(task, teamName, projectName, taskMembers);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(UUID taskId, UpdateTaskRequest request, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        taskAuthorizationService.verifyCanUpdateTask(taskId, requesterId);

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

        taskAuthorizationService.verifyCanUpdateTaskStatus(taskId, newStatus, requesterId);

        task.setStatus(newStatus);

        Task updatedTask = taskRepository.save(task);

        return TaskResponse.from(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(UUID taskId, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        taskAuthorizationService.verifyCanDeleteTask(taskId, requesterId);

        taskRepository.delete(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskMemberResponse> getTaskMembers(UUID taskId, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        teamAuthorizationService.verifyTeamMember(task.getTeamId(), requesterId);

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

        taskAuthorizationService.verifyCanAssignMember(taskId, requesterId);

        User userToAssign = userService.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.userId()));

        teamAuthorizationService.verifyTeamMember(task.getTeamId(), request.userId());
        if (task.getProjectId() != null) {
            projectAuthorizationService.verifyProjectMember(task.getProjectId(), request.userId());
        }

        if (taskMemberRepository.existsByIdTaskIdAndIdUserId(taskId, request.userId())) {
            throw new ConflictException("User " + request.userId() + " is already assigned to task " + taskId);
        }

        TaskMember taskMember = TaskMember.builder()
                .id(new TaskMember.TaskMemberId(taskId, request.userId()))
                .task(task)
                .user(userToAssign)
                .role(request.role().name())
                .build();

        TaskMember savedTaskMember = taskMemberRepository.save(taskMember);

        return TaskMemberResponse.from(savedTaskMember);
    }

    @Override
    @Transactional
    public TaskMemberResponse updateMemberRole(UUID taskId, UUID userId, TaskRole newRole, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        taskAuthorizationService.verifyCanUpdateMemberRole(taskId, requesterId);

        TaskMember taskMember = taskMemberRepository.findByIdTaskIdAndIdUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " is not a member of task " + taskId));

        taskMember.setRole(newRole.name());

        TaskMember updatedTaskMember = taskMemberRepository.save(taskMember);

        return TaskMemberResponse.from(updatedTaskMember);
    }

    @Override
    @Transactional
    public void removeMember(UUID taskId, UUID userId, UUID requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        taskAuthorizationService.verifyCanRemoveMember(taskId, userId, requesterId);

        TaskMember taskMember = taskMemberRepository.findByIdTaskIdAndIdUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " is not a member of task " + taskId));

        taskMemberRepository.delete(taskMember);
    }
}
