package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.TaskStatusChangeDTO;
import com.trelix.trelix_app.entity.TaskStatusChange;
import com.trelix.trelix_app.repository.TaskStatusChangeRepository;
import com.trelix.trelix_app.util.AppMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Transactional
@RequiredArgsConstructor
public class TaskStatusChangeService {

    private TaskStatusChangeRepository taskStatusChangeRepository;

    public TaskStatusChangeDTO getChangesByTaskId(UUID taskID) {
        TaskStatusChange taskStatusChange = taskStatusChangeRepository.findById(taskID)
                .orElseThrow(() -> new RuntimeException("Task status change not found"));
        return AppMapper.convertToTaskStatusChangeDTO(taskStatusChange);
    }
}
