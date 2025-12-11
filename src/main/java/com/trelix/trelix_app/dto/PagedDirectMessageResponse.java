//package com.trelix.trelix_app.dto;
//
//import com.trelix.trelix_app.entity.Message;
//import com.trelix.trelix_app.service.UserService;
//import org.springframework.data.domain.Page;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public record PagedDirectMessageResponse(
//        List<DirectMessageMessageResponse> messages,
//        int currentPage,
//        int totalPages,
//        long totalElements
//) {
//    public static PagedDirectMessageResponse from(Page<Message> messagePage, UserService userService) {
//        List<DirectMessageMessageResponse> messageResponses = messagePage.getContent().stream()
//                .map(message -> {
//                    String senderName = userService.findById(message.getSenderId())
//                            .map(user -> user.getUsername())
//                            .orElse("Unknown User");
//                    return DirectMessageMessageResponse.from(message, senderName);
//                })
//                .collect(Collectors.toList());
//
//        return new PagedDirectMessageResponse(
//                messageResponses,
//                messagePage.getNumber(),
//                messagePage.getTotalPages(),
//                messagePage.getTotalElements()
//        );
//    }
//}
