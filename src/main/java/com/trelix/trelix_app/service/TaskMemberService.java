package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.MemberDTO;
import com.trelix.trelix_app.entity.Task;
import com.trelix.trelix_app.entity.TaskMember;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.TaskMemberRepository;
import com.trelix.trelix_app.repository.TaskRepository;
import com.trelix.trelix_app.repository.UserRepository;
import com.trelix.trelix_app.util.AppMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskMemberService {

    private final TaskMemberRepository taskMemberRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authService;

    public List<MemberDTO> getTaskMembers(UUID taskId, UUID userId) {
        authService.checkTaskAccessByTaskId(taskId, userId);
        return taskMemberRepository.findByTaskId(taskId)
                .stream()
                .map(taskMember -> AppMapper.convertToMemberDto(taskMember.getUser()))
                .toList();
    }

    public void assignUserToTask(UUID taskId, UUID userIdToAssign, UUID requestingUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        authService.checkProjectAdminAccess(task.getProject().getTeam().getId(), task.getProject().getId(), requestingUserId);
        User userToAssign = userRepository.findById(userIdToAssign)
                .orElseThrow(() -> new ResourceNotFoundException("User to assign not found with id: " + userIdToAssign));

        if (taskMemberRepository.existsByTaskIdAndUserId(taskId, userIdToAssign)) {
            return; // User is already a member
        }

        TaskMember taskMember = new TaskMember();
        taskMember.setTask(task);
        taskMember.setUser(userToAssign);
        taskMemberRepository.save(taskMember);
    }

    public void removeUserFromTask(UUID taskId, UUID userIdToRemove, UUID requestingUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        authService.checkProjectAdminAccess(task.getProject().getTeam().getId(), task.getProject().getId(), requestingUserId);
        TaskMember taskMember = taskMemberRepository.findByTaskIdAndUserId(taskId, userIdToRemove)
                .orElseThrow(() -> new ResourceNotFoundException("Task member not found for the given task and user"));
        taskMemberRepository.delete(taskMember);
    }
}
