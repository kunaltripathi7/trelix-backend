package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.TaskStatusChangeDTO;
import com.trelix.trelix_app.entity.TaskStatusChange;
import com.trelix.trelix_app.repository.TaskStatusChangeRepository;
import com.trelix.trelix_app.util.AppMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskStatusChangeService {

    private final TaskStatusChangeRepository taskStatusChangeRepository;

    public List<TaskStatusChangeDTO> getChangesByTaskId(UUID taskID) {
        List<TaskStatusChange> taskStatusChanges = taskStatusChangeRepository.findByTaskId(taskID);
        // It's better to return an empty list than to throw an error if no changes are found.
        return taskStatusChanges.stream()
                .map(AppMapper::convertToTaskStatusChangeDTO)
                .toList();
    }
}
