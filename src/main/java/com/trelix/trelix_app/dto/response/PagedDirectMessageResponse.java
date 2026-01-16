package com.trelix.trelix_app.dto.response;

import com.trelix.trelix_app.entity.Message;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public record PagedDirectMessageResponse(
        List<DirectMessageMessageResponse> messages,
        int currentPage,
        int totalPages,
        long totalElements
) {
    public static PagedDirectMessageResponse from(Page<Message> messagePage) {
        List<DirectMessageMessageResponse> messageResponses = messagePage.getContent().stream()
                .map(message -> {
                    String senderName = message.getSender() != null ? message.getSender().getName() : "Unknown User";
                    return DirectMessageMessageResponse.from(message, senderName);
                })
                .collect(Collectors.toList());

        return new PagedDirectMessageResponse(
                messageResponses,
                messagePage.getNumber(),
                messagePage.getTotalPages(),
                messagePage.getTotalElements()
        );
    }
}




