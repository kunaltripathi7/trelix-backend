package com.trelix.trelix_app.dto.response;

import com.trelix.trelix_app.entity.Message;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.stream.Collectors;

public record PagedMessageResponse(
        List<MessageResponse> messages,
        int currentPage,
        int totalPages,
        long totalElements
) {
    public static PagedMessageResponse from(Page<Message> messagePage) {
        List<MessageResponse> messageResponses = messagePage.getContent().stream()
                .map(MessageResponse::from)
                .collect(Collectors.toList());

        return new PagedMessageResponse(
                messageResponses,
                messagePage.getNumber(),
                messagePage.getTotalPages(),
                messagePage.getTotalElements()
        );
    }
}




