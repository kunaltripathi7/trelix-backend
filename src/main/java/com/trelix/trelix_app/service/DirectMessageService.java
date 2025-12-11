//package com.trelix.trelix_app.service;
//
//import com.trelix.trelix_app.dto.DirectMessageConversationResponse;
//import com.trelix.trelix_app.dto.DirectMessageDetailResponse;
//import com.trelix.trelix_app.dto.DirectMessageMessageResponse;
//import com.trelix.trelix_app.dto.DirectMessageResponse;
//import com.trelix.trelix_app.dto.EditDirectMessageRequest;
//import com.trelix.trelix_app.dto.PagedDirectMessageResponse;
//import com.trelix.trelix_app.dto.SendDirectMessageRequest;
//
//import java.util.List;
//import java.util.UUID;
//
//public interface DirectMessageService {
//    DirectMessageResponse createOrGetDirectMessage(UUID currentUserId, UUID otherUserId);
//
//    List<DirectMessageConversationResponse> getAllDirectMessages(UUID userId);
//
//    DirectMessageDetailResponse getDirectMessageById(UUID dmId, UUID requesterId);
//
//    PagedDirectMessageResponse getMessages(UUID dmId, int page, int size, UUID requesterId);
//
//    DirectMessageMessageResponse sendMessage(UUID dmId, SendDirectMessageRequest request, UUID senderId);
//
//    DirectMessageMessageResponse editMessage(UUID dmId, UUID messageId, EditDirectMessageRequest request, UUID requesterId);
//
//    void deleteMessage(UUID dmId, UUID messageId, UUID requesterId);
//}
