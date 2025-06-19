package com.trelix.trelix_app.util;

import com.trelix.trelix_app.dto.*;
import com.trelix.trelix_app.entity.*;

public class AppMapper {
    public static MemberDTO convertToTeamMemberDto(TeamUser teamUser) {
        User member = teamUser.getUser();
        return MemberDTO.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .role(teamUser.getRole().toString())
                .build();
    }


    public static ProjectDetailResponse convertToProjectDetailResponse(Project project) {
        return ProjectDetailResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus().toString())
                .createdAt(project.getCreatedAt())
                .tasks(project.getTasks().stream().map(AppMapper::convertToTaskDTO).toList())
                .channels(project.getChannels().stream().map(AppMapper::convertToChannelDto).toList())
                .build();
    }

    public static ProjectResponse convertToProjectResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus().toString())
                .build();
    }


    public static ChannelDTO convertToChannelDto(Channel channel) {
        return ChannelDTO.builder()
                .id(channel.getId())
                .name(channel.getName())
                .isPrivate(channel.getIsPrivate())
                .createdAt(channel.getCreatedAt())
                .teamId(channel.getTeam() != null ? channel.getTeam().getId() : null)
                .projectId(channel.getProject() != null ? channel.getProject().getId() : null)
                .description(channel.getDescription())
                .build();
    }


    public static TaskDTO convertToTaskDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().toString())
                .priority(task.getPriority().toString())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .assignedToId(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null)
                .assignedToName(task.getAssignedTo() != null ? task.getAssignedTo().getUsername() : null)
                .build();
    }

    public static TaskDetailsDTO convertToTaskDetailsDTO(Task task) {
        return TaskDetailsDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().toString())
                .priority(task.getPriority().toString())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .assignedToId(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null)
                .assignedToName(task.getAssignedTo() != null ? task.getAssignedTo().getUsername() : null)
                .comments(task.getComments() != null ? task.getComments().stream().map(AppMapper::convertToCommentDTO).toList() : null)
                .attachments(task.getAttachments() != null ? task.getAttachments().stream().map(AppMapper::convertToAttachmentDTO).toList() : null)
                .statusChanges(task.getStatusChanges() != null ? task.getStatusChanges().stream().map(AppMapper::convertToTaskStatusChangeDTO).toList() : null)
                .events(task.getEvents() != null ? task.getEvents().stream().map(AppMapper::convertToEventDTO).toList() : null)
                .build();
    }

    public static EventDTO convertToEventDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId().toString())
                .title(event.getTitle())
                .description(event.getDescription())
                .startTime(event.getCreatedAt())
                .endTime(event.getEndTime())
                .projectId(event.getProject() != null ? event.getProject().getId().toString() : null)
                .teamId(event.getTeam() != null ? event.getTeam().getId().toString() : null)
                .taskId(event.getTask() != null ? event.getTask().getId().toString() : null)
                .createdBy(event.getCreatedBy() != null ? event.getCreatedBy().toString() : null)
                .createdAt(event.getCreatedAt() != null ? event.getCreatedAt() : null)
                .build();
    }

    public static TaskCommentDTO convertToCommentDTO(TaskComment comment) {
        return TaskCommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getUser() != null ? comment.getUser().getId() : null)
                .authorName(comment.getUser() != null ? comment.getUser().getUsername() : null)
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public static AttachmentDTO convertToAttachmentDTO(Attachment attachment) {
        return AttachmentDTO.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .fileUrl(attachment.getUrl())
                .fileType(attachment.getFileType())
                .fileSize(attachment.getFileSize())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
    public static Attachment convertToAttachment(AttachmentDTO attachmentDTO, User uploadedBy, Task task, Message message) {
        return Attachment.builder()
                .fileName(attachmentDTO.getFileName())
                .url(attachmentDTO.getFileUrl())
                .fileType(attachmentDTO.getFileType())
                .fileSize(attachmentDTO.getFileSize())
                .uploadedBy(uploadedBy)
                .task(task)
                .message(message)
                .createdAt(attachmentDTO.getCreatedAt())
                .build();

    }



    public static TaskStatusChangeDTO convertToTaskStatusChangeDTO(TaskStatusChange taskStatusChange) {
        return TaskStatusChangeDTO.builder()
                .oldStatus(taskStatusChange.getPreviousStatus().toString())
                .newStatus(taskStatusChange.getNewStatus().toString())
                .changedByName(taskStatusChange.getChangedBy() != null ? taskStatusChange.getChangedBy().getUsername() : null)
                .changedAt(taskStatusChange.getChangedAt())
                .build();
    }

    public static MessageDetailDTO convertToMessageDetailDTO(Message message) {
        return MessageDetailDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSender() != null ? message.getSender().getId() : null)
                .sentAt(message.getCreatedAt())
                .comments(message.getComments() != null ? message.getComments().stream().map(AppMapper::convertToMessageCommentDTO).toList() : null)
                .senderUsername(message.getSender() != null ? message.getSender().getUsername() : null)
                .attachments(message.getAttachments() != null ? message.getAttachments().stream().map(AppMapper::convertToAttachmentDTO).toList() : null)
                .build();
    }

    public static MessageSummaryDTO convertToMessageSummaryDTO(Message message) {
        return MessageSummaryDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSender().getId())
                .sentAt(message.getCreatedAt())
                .senderUsername(message.getSender().getUsername())
                .build();
    }

    public static MessageCommentDTO convertToMessageCommentDTO(MessageComment comment) {
        return MessageCommentDTO.builder()
                .id(comment.getId())
                .username(comment.getUser() != null ? comment.getUser().getUsername() : null)
                .userId(comment.getUser() != null ? comment.getUser().getId() : null)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public static MessageSummaryDTO convertToMessageSummaryDTO(MessageComment comment) {
        return MessageSummaryDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .senderId(comment.getUser() != null ? comment.getUser().getId() : null)
                .sentAt(comment.getCreatedAt())
                .senderUsername(comment.getUser() != null ? comment.getUser().getUsername() : null)
                .build();
    }

    public static NotificationDTO convertToNotificationDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .taskId(notification.getTask() != null ? notification.getTask().getId() : null)
                .messageId(notification.getMessage() != null ? notification.getMessage().getId() : null)
                .actorId(notification.getActor() != null ? notification.getActor().getId() : null)
                .actorName(notification.getActor() != null ? notification.getActor().getUsername() : null)
                .build();
    }
}
