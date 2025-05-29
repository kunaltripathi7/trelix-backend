package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.MemberDTO;
import com.trelix.trelix_app.entity.Task;
import com.trelix.trelix_app.entity.TaskMember;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.TaskMemberRepository;
import com.trelix.trelix_app.repository.TaskRepository;
import com.trelix.trelix_app.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskMemberService {

    private final TaskService taskService;
    private final TaskMemberRepository taskMemberRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public List<MemberDTO> getTaskMembers(UUID taskId) {
        return taskMemberRepository.findByTaskId(taskId)
                .stream()
                .map(taskMember -> MemberDTO.builder().id(taskMember.getUser().getId())
                                        .username(taskMember.getUser().getUsername())
                                .email(taskMember.getUser().getEmail())
                                .build()).toList();
    }

    public void assignUserToTask(UUID taskId, UUID userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        TaskMember taskMember = new TaskMember();
        taskMember.setTask(task);
        taskMember.setUser(user);
        taskMemberRepository.save(taskMember);
    }

    public void removeUserFromTask(UUID taskId, UUID userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        TaskMember taskMember = taskMemberRepository.findByTaskIdAndUserId(task.getId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task member not found"));
        taskMemberRepository.delete(taskMember);
    }
}
