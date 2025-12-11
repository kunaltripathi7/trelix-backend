//package com.trelix.trelix_app.dto;
//
//import com.trelix.trelix_app.entity.DirectMessage;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//public record DirectMessageConversationResponse(
//        UUID id,
//        UUID otherUserId,
//        String otherUserName,
//        String otherUserEmail,
//        String lastMessageContent, // nullable - preview of last message
//        LocalDateTime lastMessageAt, // nullable
//        LocalDateTime createdAt
//) {
//    public static DirectMessageConversationResponse from(
//            DirectMessage dm,
//            UUID requesterId,
//            String otherUserName,
//            String otherUserEmail,
//            String lastMessageContent,
//            LocalDateTime lastMessageAt) {
//
//        UUID otherUserId = dm.getUser1Id().equals(requesterId) ? dm.getUser2Id() : dm.getUser1Id();
//
//        return new DirectMessageConversationResponse(
//                dm.getId(),
//                otherUserId,
//                otherUserName,
//                otherUserEmail,
//                lastMessageContent,
//                lastMessageAt,
//                dm.getCreatedAt()
//        );
//    }
//}
