package com.trelix.trelix_app.util;

import com.trelix.trelix_app.dto.*;
import com.trelix.trelix_app.entity.*;

import java.util.stream.Collectors;

public class AppMapper {

    public static MemberDTO convertToMemberDto(User user) {
        return MemberDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public static MemberDTO convertToTeamMemberDto(TeamUser teamUser) {
        MemberDTO dto = convertToMemberDto(teamUser.getUser());
        dto.setRole(teamUser.getRole().toString());
        return dto;
    }

    public static MemberDTO convertToProjectMemberDto(ProjectMember projectMember) {
        MemberDTO dto = convertToMemberDto(projectMember.getUser());
        dto.setRole(projectMember.getRole().toString());
        return dto;
    }

    public static MemberDTO convertToTaskMemberDto(TaskMember taskMember) {
        MemberDTO dto = convertToMemberDto(taskMember.getUser());
        dto.setRole(taskMember.getRole().toString());
        return dto;
    }

    public static TeamResponse convertToTeamResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .build();
    }

    public static TeamDetailsResponse convertToTeamDetailsResponse(Team team) {
        return TeamDetailsResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .members(team.getTeamUsers().stream().map(AppMapper::convertToTeamMemberDto).collect(Collectors.toList()))
                .projects(team.getProjects().stream().map(AppMapper::convertToProjectResponse).collect(Collectors.toList()))
                .channels(team.getChannels().stream().map(AppMapper::convertToChannelDto).collect(Collectors.toList()))
                .build();
    }

    public static ProjectDetailResponse convertToProjectDetailResponse(Project project) {
        return ProjectDetailResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus().toString())
                .createdAt(project.getCreatedAt())
                .tasks(project.getTasks().stream().map(AppMapper::convertToTaskDTO).collect(Collectors.toList()))
                .channels(project.getChannels().stream().map(AppMapper::convertToChannelDto).collect(Collectors.toList()))
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
                .members(task.getTaskMembers().stream().map(AppMapper::convertToTaskMemberDto).collect(Collectors.toList()))
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
                .members(task.getTaskMembers().stream().map(AppMapper::convertToTaskMemberDto).collect(Collectors.toList()))
                .comments(task.getComments().stream().map(AppMapper::convertToCommentDTO).collect(Collectors.toList()))
                .attachments(task.getAttachments().stream().map(AppMapper::convertToAttachmentDTO).collect(Collectors.toList()))
                .statusChanges(task.getStatusChanges().stream().map(AppMapper::convertToTaskStatusChangeDTO).collect(Collectors.toList()))
                .events(task.getEvents().stream().map(AppMapper::convertToEventDTO).collect(Collectors.toList()))
                .build();
    }

    public static EventDTO convertToEventDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId().toString())
                .title(event.getTitle())
                .description(event.getDescription())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .projectId(event.getProject() != null ? event.getProject().getId().toString() : null)
                .teamId(event.getTeam() != null ? event.getTeam().getId().toString() : null)
                .taskId(event.getTask() != null ? event.getTask().getId().toString() : null)
                .createdBy(event.getCreatedBy() != null ? event.getCreatedBy().getId().toString() : null)
                .createdAt(event.getCreatedAt())
                .build();
    }

    public static CommentDTO convertToCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .taskId(comment.getTask() != null ? comment.getTask().getId() : null)
                .messageId(comment.getMessage() != null ? comment.getMessage().getId() : null)
                .user(convertToMemberDto(comment.getUser()))
                .content(comment.getContent())
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

    public static AttachmentResponse convertToAttachmentResponse(Attachment attachment, User uploader) {
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getFileType(),
                attachment.getFileSize(),
                attachment.getUrl(),
                attachment.getUploadedBy(),
                uploader != null ? uploader.getUsername() : null,
                attachment.getEntityType(),
                attachment.getEntityId(),
                attachment.getCreatedAt()
        );
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
                .comments(message.getComments().stream().map(AppMapper::convertToCommentDTO).collect(Collectors.toList()))
                .senderUsername(message.getSender() != null ? message.getSender().getUsername() : null)
                .attachments(message.getAttachments().stream().map(AppMapper::convertToAttachmentDTO).collect(Collectors.toList()))
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
