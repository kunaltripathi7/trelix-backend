package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.request.AddChannelMemberRequest;
import com.trelix.trelix_app.dto.response.ChannelDetailResponse;
import com.trelix.trelix_app.dto.response.ChannelMemberResponse;
import com.trelix.trelix_app.dto.response.ChannelResponse;
import com.trelix.trelix_app.dto.request.CreateChannelRequest;
import com.trelix.trelix_app.dto.request.UpdateChannelRequest;
import com.trelix.trelix_app.entity.Channel;
import com.trelix.trelix_app.entity.ChannelMember;
import com.trelix.trelix_app.entity.Project;
import com.trelix.trelix_app.entity.Team;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.ChannelRole;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ChannelMemberRepository;
import com.trelix.trelix_app.repository.ChannelRepository;
import com.trelix.trelix_app.repository.ProjectRepository;
import com.trelix.trelix_app.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final AuthorizationService authorizationService;

    @Override
    @Transactional
    public ChannelResponse createChannel(CreateChannelRequest request, UUID creatorId) {
        boolean isTeamChannel = request.teamId() != null && request.projectId() == null;
        boolean isProjectChannel = request.teamId() != null && request.projectId() != null;
        boolean isAdHocChannel = request.teamId() == null && request.projectId() == null;
        Team team = null;
        Project project = null;

        if (isProjectChannel) {
            authorizationService.verifyProjectAdmin(request.projectId(), creatorId);
            project = projectRepository.findById(request.projectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with the given ID"));
            team = project.getTeam();
        } else if (isTeamChannel) {
            authorizationService.verifyTeamAdmin(request.teamId(), creatorId);
            team = teamRepository.findById(request.teamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with the given ID"));
        }

        Channel channel = Channel.builder()
                .teamId(request.teamId())
                .projectId(request.projectId())
                .team(team)
                .project(project)
                .name(request.name())
                .build();

        Channel savedChannel = channelRepository.save(channel);

        if (isAdHocChannel) {
            User creator = userService.findById(creatorId);

            ChannelMember channelMember = ChannelMember.builder()
                    .id(new ChannelMember.ChannelMemberId(savedChannel.getId(), creatorId))
                    .channel(savedChannel)
                    .user(creator)
                    .role(ChannelRole.OWNER)
                    .build();
            channelMemberRepository.save(channelMember);
        }

        return ChannelResponse.from(savedChannel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelResponse> getChannels(UUID teamId, UUID projectId, String type, UUID requesterId) {
        if (projectId != null) {
            authorizationService.verifyProjectMembership(projectId, requesterId);
            return channelRepository.findByProjectId(projectId).stream()
                    .map(ChannelResponse::from)
                    .collect(Collectors.toList());
        } else if (teamId != null) {
            authorizationService.verifyTeamMembership(teamId, requesterId);
            return channelRepository.findByTeamIdAndProjectIdIsNull(teamId).stream()
                    .map(ChannelResponse::from)
                    .collect(Collectors.toList());
        } else if ("AD_HOC".equalsIgnoreCase(type)) {
            return channelRepository.findAdHocChannelsByUserId(requesterId).stream()
                    .map(ChannelResponse::from)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDetailResponse getChannelById(UUID channelId, UUID requesterId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channelId));

        verifyChannelAccess(channel, requesterId);

        String teamName = channel.getTeam() != null ? channel.getTeam().getName() : null;
        String projectName = channel.getProject() != null ? channel.getProject().getName() : null;
        List<ChannelMember> members = channelMemberRepository.findByIdChannelId(channelId);

        return ChannelDetailResponse.from(channel, teamName, projectName, members);
    }

    @Override
    @Transactional
    public ChannelResponse updateChannel(UUID channelId, UpdateChannelRequest request, UUID requesterId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channelId));

        verifyChannelAdmin(channel, requesterId);

        if (request.name() != null && !request.name().equals(channel.getName())) {
            // Check for duplicate names within scope
            if (channel.getProjectId() != null) {
                if (channelRepository.existsByNameAndProjectId(request.name(), channel.getProjectId())) {
                    throw new IllegalArgumentException("Channel name already exists in this project");
                }
            } else if (channel.getTeamId() != null) {
                if (channelRepository.existsByNameAndTeamId(request.name(), channel.getTeamId())) {
                    throw new IllegalArgumentException("Channel name already exists in this team");
                }
            }
            channel.setName(request.name());
        }

        return ChannelResponse.from(channelRepository.save(channel));
    }

    @Override
    @Transactional
    public void deleteChannel(UUID channelId, UUID requesterId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channelId));

        verifyChannelAdmin(channel, requesterId);
        channelRepository.delete(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelMemberResponse> getChannelMembers(UUID channelId, UUID requesterId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channelId));

        verifyChannelAccess(channel, requesterId);

        return channelMemberRepository.findByIdChannelId(channelId).stream()
                .map(ChannelMemberResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChannelMemberResponse addMember(UUID channelId, AddChannelMemberRequest request, UUID requesterId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channelId));

        // Only ad-hoc channels typically support explicit member management this way,
        // OR adding team members to private channels if that feature existed.
        // For now, assuming this is mainly for ad-hoc or specifically adding someone to
        // a channel they don't have auto-access to?
        // But the schema implies team/project channels don't use ChannelMember table
        // for access (implicit).
        // So this is strictly for AD_HOC channels or if we want to add 'extra' members?
        // Let's restrict to AD_HOC for now or require admin.

        verifyChannelAdmin(channel, requesterId);

        if (channelMemberRepository.existsByIdChannelIdAndIdUserId(channelId, request.userId())) {
            throw new IllegalArgumentException("User is already a member of this channel");
        }

        User user = userService.findById(request.userId());

        ChannelMember member = ChannelMember.builder()
                .id(new ChannelMember.ChannelMemberId(channelId, request.userId()))
                .channel(channel)
                .user(user)
                .role(request.role() != null ? request.role() : ChannelRole.MEMBER)
                .build();

        return ChannelMemberResponse.from(channelMemberRepository.save(member));
    }

    @Override
    @Transactional
    public void removeMember(UUID channelId, UUID userId, UUID requesterId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channelId));

        verifyChannelAdmin(channel, requesterId);

        // Cannot remove self if owner, etc. logic can be added here.

        ChannelMember member = channelMemberRepository.findByIdChannelIdAndIdUserId(channelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in channel"));

        channelMemberRepository.delete(member);
    }

    private void verifyChannelAccess(Channel channel, UUID userId) {
        if (channel.getProjectId() != null) {
            authorizationService.verifyProjectMembership(channel.getProjectId(), userId);
        } else if (channel.getTeamId() != null) {
            authorizationService.verifyTeamMembership(channel.getTeamId(), userId);
        } else {
            if (!channelMemberRepository.existsByIdChannelIdAndIdUserId(channel.getId(), userId)) {
                throw new org.springframework.security.access.AccessDeniedException("Access denied to channel");
            }
        }
    }

    private void verifyChannelAdmin(Channel channel, UUID userId) {
        if (channel.getProjectId() != null) {
            authorizationService.verifyProjectAdmin(channel.getProjectId(), userId);
        } else if (channel.getTeamId() != null) {
            authorizationService.verifyTeamAdmin(channel.getTeamId(), userId);
        } else {
            ChannelMember member = channelMemberRepository.findByIdChannelIdAndIdUserId(channel.getId(), userId)
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException(
                            "Access denied to channel"));

            if (member.getRole() != ChannelRole.OWNER) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Only channel owner can perform this action");
            }
        }
    }
}




