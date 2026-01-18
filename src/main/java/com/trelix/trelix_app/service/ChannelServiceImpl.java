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
import com.trelix.trelix_app.enums.ChannelType;
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
        boolean isProjectChannel = request.projectId() != null;
        Team team = null;
        Project project = null;

        if (isProjectChannel) {
            project = projectRepository.findById(request.projectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with the given ID"));
            authorizationService.verifyProjectAdmin(request.projectId(), creatorId);
            team = project.getTeam();
        } else {
            team = teamRepository.findById(request.teamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with the given ID"));
            authorizationService.verifyTeamAdmin(request.teamId(), creatorId);
        }

        Channel channel = Channel.builder()
                .team(team)
                .teamId(team.getId())
                .project(project)
                .projectId(project != null ? project.getId() : null)
                .name(request.name())
                .build();

        Channel savedChannel = channelRepository.save(channel);

        User creator = userService.findById(creatorId);
        ChannelMember creatorMember = ChannelMember.builder()
                .id(new ChannelMember.ChannelMemberId(savedChannel.getId(), creatorId))
                .channel(savedChannel)
                .user(creator)
                .role(ChannelRole.OWNER)
                .build();
        channelMemberRepository.save(creatorMember);

        return ChannelResponse.from(savedChannel);
    }

    @Override
    @Transactional
    public ChannelResponse startDirectMessage(UUID otherUserId, UUID requesterId) {
        if (otherUserId.equals(requesterId)) {
            throw new IllegalArgumentException("Cannot start a DM with yourself");
        }

        User requester = userService.findById(requesterId);
        User otherUser = userService.findById(otherUserId);

        Channel existingDm = channelRepository.findExistingDmBetweenUsers(requesterId, otherUserId);
        if (existingDm != null) { // idempotent
            return ChannelResponse.from(existingDm);
        }

        Channel channel = Channel.builder()
                .name("DM: " + requester.getName() + " & " + otherUser.getName())
                .build();

        Channel savedChannel = channelRepository.save(channel);

        ChannelMember member1 = ChannelMember.builder()
                .id(new ChannelMember.ChannelMemberId(savedChannel.getId(), requesterId))
                .channel(savedChannel)
                .user(requester)
                .role(ChannelRole.OWNER)
                .build();

        ChannelMember member2 = ChannelMember.builder()
                .id(new ChannelMember.ChannelMemberId(savedChannel.getId(), otherUserId))
                .channel(savedChannel)
                .user(otherUser)
                .role(ChannelRole.MEMBER)
                .build();

        channelMemberRepository.save(member1);
        channelMemberRepository.save(member2);

        return ChannelResponse.from(savedChannel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelResponse> getChannels(UUID teamId, UUID projectId, ChannelType type, UUID requesterId) {
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
        } else if (type == ChannelType.DIRECT) {
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

        ChannelMember member = channelMemberRepository.findByIdChannelIdAndIdUserId(channelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in channel"));

        if (member.getRole() == ChannelRole.OWNER) {
            long ownerCount = channelMemberRepository.findByIdChannelId(channelId).stream()
                    .filter(m -> m.getRole() == ChannelRole.OWNER)
                    .count();
            if (ownerCount <= 1) {
                throw new IllegalArgumentException("Cannot remove the last owner of the channel");
            }
        }

        channelMemberRepository.delete(member);
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyChannelAccess(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channelId));
        verifyChannelAccess(channel, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyChannelAdmin(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channelId));
        verifyChannelAdmin(channel, userId);
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
