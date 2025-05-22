package com.trelix.trelix_app.util;

import com.trelix.trelix_app.dto.*;
import com.trelix.trelix_app.entity.*;

public class AppMapper {
    public static TeamMemberDTO convertToTeamMemberDto(TeamUser teamUser) {
        User member = teamUser.getUser();
        return TeamMemberDTO.builder()
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
                .comments(task.getComments() != null
                        ? task.getComments().stream().map(AppMapper::convertToCommentDTO).toList()
                        : null)
                .attachments(task.getAttachments() != null
                        ? task.getAttachments().stream().map(AppMapper::convertToAttachmentDTO).toList()
                        : null)
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
                .uploadedById(attachment.getUploadedBy() != null ? attachment.getUploadedBy().getId() : null)
                .uploadedByName(attachment.getUploadedBy() != null ? attachment.getUploadedBy().getUsername() : null)
                .uploadedAt(attachment.getCreatedAt())
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



}
