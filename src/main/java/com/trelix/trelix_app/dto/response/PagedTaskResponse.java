package com.trelix.trelix_app.dto.response;

import com.trelix.trelix_app.entity.Task;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public record PagedTaskResponse(
        List<TaskResponse> tasks,
        int currentPage,
        int totalPages,
        long totalElements
) {
    public static PagedTaskResponse from(Page<Task> taskPage) {
        List<TaskResponse> taskResponses = taskPage.getContent().stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());

        return new PagedTaskResponse(
                taskResponses,
                taskPage.getNumber(),
                taskPage.getTotalPages(),
                taskPage.getTotalElements()
        );
    }
}




