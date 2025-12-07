package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.AddChannelMemberRequest;
import com.trelix.trelix_app.dto.ChannelDetailResponse;
import com.trelix.trelix_app.dto.ChannelMemberResponse;
import com.trelix.trelix_app.dto.ChannelResponse;
import com.trelix.trelix_app.dto.CreateChannelRequest;
import com.trelix.trelix_app.dto.UpdateChannelRequest;
import com.trelix.trelix_app.entity.Channel;
import com.trelix.trelix_app.entity.ChannelMember;
import com.trelix.trelix_app.entity.Project;
import com.trelix.trelix_app.entity.Team;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.ChannelRole;
import com.trelix.trelix_app.exception.BadRequestException;
import com.trelix.trelix_app.exception.ConflictException;
import com.trelix.trelix_app.exception.ForbiddenException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ChannelMemberRepository;
import com.trelix.trelix_app.repository.ChannelRepository;
import com.trelix.trelix_app.repository.ProjectRepository;
import com.trelix.trelix_app.repository.TeamRepository; // Assuming this exists for team validation
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final TeamRepository teamRepository; // For team existence validation
    private final ProjectRepository projectRepository; // For project existence and team association validation
    private final UserService userService; // To fetch User details for ChannelMember
    private final TeamService teamService; // To get team name
    private final ProjectService projectService; // To get project name
    private final TeamAuthorizationService teamAuthorizationService;
    private final ProjectAuthorizationService projectAuthorizationService;
    private final ChannelAuthorizationService channelAuthorizationService;

    public ChannelServiceImpl(ChannelRepository channelRepository,
                              ChannelMemberRepository channelMemberRepository,
                              TeamRepository teamRepository,
                              ProjectRepository projectRepository,
                              UserService userService,
                              TeamService teamService,
                              ProjectService projectService,
                              TeamAuthorizationService teamAuthorizationService,
                              ProjectAuthorizationService projectAuthorizationService,
                              ChannelAuthorizationService channelAuthorizationService) {
        this.channelRepository = channelRepository;
        this.channelMemberRepository = channelMemberRepository;
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.teamService = teamService;
        this.projectService = projectService;
        this.teamAuthorizationService = teamAuthorizationService;
        this.projectAuthorizationService = projectAuthorizationService;
        this.channelAuthorizationService = channelAuthorizationService;
    }

    @Override
    @Transactional
    public ChannelResponse createChannel(CreateChannelRequest request, UUID creatorId) {
        // Determine channel type and perform initial validations
        boolean isTeamChannel = request.teamId() != null && request.projectId() == null;
        boolean isProjectChannel = request.teamId() != null && request.projectId() != null;
        boolean isAdHocChannel = request.teamId() == null && request.projectId() == null;

        if (request.teamId() == null && request.projectId() != null) {
            throw new BadRequestException("Cannot create a project channel without a team ID.");
        }

        // Validate team and project existence and creator's membership
        if (request.teamId() != null) {
            Team team = teamRepository.findById(request.teamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + request.teamId()));
            teamAuthorizationService.verifyTeamMember(request.teamId(), creatorId);
        }

        if (request.projectId() != null) {
            Project project = projectRepository.findById(request.projectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + request.projectId()));
            if (!project.getTeamId().equals(request.teamId())) {
                throw new BadRequestException("Project " + request.projectId() + " does not belong to team " + request.teamId());
            }
            projectAuthorizationService.verifyProjectMember(request.projectId(), creatorId);
        }

        // Create Channel entity
        Channel channel = Channel.builder()
                .teamId(request.teamId())
                .projectId(request.projectId())
                .name(request.name())
                // Assuming description and isPrivate are new fields in Channel entity
                // If not, these lines should be removed or adapted
                // .description(request.description())
                // .isPrivate(request.isPrivate())
                .build();

        Channel savedChannel = channelRepository.save(channel);

        // For Ad-hoc channels, add creator as OWNER
        if (isAdHocChannel) {
            User creator = userService.findById(creatorId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + creatorId));

            ChannelMember channelMember = ChannelMember.builder()
                    .id(new ChannelMember.ChannelMemberId(savedChannel.getId(), creatorId))
                    .channel(savedChannel)
                    .user(creator)
                    .role(ChannelRole.OWNER.name())
                    .build();
            channelMemberRepository.save(channelMember);
        }

        return ChannelResponse.from(savedChannel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelResponse> getChannels(UUID teamId, UUID projectId, String type, UUID requesterId) {
        List<Channel> channels = new java.util.ArrayList<>();

        // 1. Verify requester's general access if teamId or projectId is provided
        if (teamId != null) {
            teamAuthorizationService.verifyTeamMember(teamId, requesterId);
            if (projectId != null) {
                projectAuthorizationService.verifyProjectMember(projectId, requesterId);
            }
        }

        // 2. Fetch channels based on filters
        if (projectId != null) {
            channels.addAll(channelRepository.findByProjectId(projectId));
        } else if (teamId != null) {
            // If only teamId is provided, get team-level channels
            channels.addAll(channelRepository.findByTeamIdAndProjectIdIsNull(teamId));
        }

        // Add ad-hoc channels the user is a member of
        // This is a separate query because ad-hoc channels don't have teamId/projectId
        channels.addAll(channelRepository.findAdHocChannelsByUserId(requesterId));

        // Filter by type if specified
        if (type != null && !type.isEmpty()) {
            channels = channels.stream().filter(channel -> {
                String channelType;
                if (channel.getTeamId() != null && channel.getProjectId() != null) {
                    channelType = "PROJECT";
                } else if (channel.getTeamId() != null) {
                    channelType = "TEAM";
                } else {
                    channelType = "AD_HOC";
                }
                return channelType.equalsIgnoreCase(type);
            }).collect(Collectors.toList());
        }

        // 3. Filter channels to only include those the requester has access to
        // This is crucial because the initial queries might return channels the user shouldn't see
        List<ChannelResponse> accessibleChannels = channels.stream()
                .filter(channel -> {
                    try {
                        verifyChannelAccess(channel.getId(), requesterId);
                        return true;
                    } catch (ForbiddenException e) {
                        return false;
                    } catch (ResourceNotFoundException e) {
                        return false; // Channel might have been deleted concurrently
                    }
                })
                .map(ChannelResponse::from)
                .collect(Collectors.toList());

        return accessibleChannels;
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDetailResponse getChannelById(UUID channelId, UUID requesterId) {
        // 1. Find the channel
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channelId));

        // 2. Verify requester has access to the channel
        verifyChannelAccess(channelId, requesterId);

        // 3. Determine channel type
        boolean isAdHocChannel = channel.getTeamId() == null && channel.getProjectId() == null;

        String teamName = null;
        String projectName = null;
        Integer memberCount = null;
        List<ChannelMember> channelMembers = new java.util.ArrayList<>();

        // 4. Fetch related names and member info based on channel type
        if (channel.getTeamId() != null) {
            teamName = teamService.findById(channel.getTeamId())
                    .map(Team::getName)
                    .orElse("Unknown Team"); // Assuming Team entity has getName()
        }
        if (channel.getProjectId() != null) {
            // Assuming projectService has a method like getProjectById that returns a DTO with projectName
            // Or you might need to fetch the Project entity directly
            Project project = projectRepository.findById(channel.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + channel.getProjectId()));
            projectName = project.getName();
        }

        if (isAdHocChannel) {
            channelMembers = channelMemberRepository.findByIdChannelId(channelId);
        } else {
            // For Team/Project channels, member count is derived from parent entity
            // This is a placeholder, actual implementation would query team/project service
            if (channel.getTeamId() != null && channel.getProjectId() == null) { // Team channel
                // Assuming teamService has a method to count members
                // memberCount = teamService.countTeamMembers(channel.getTeamId());
                memberCount = 0; // Placeholder
            } else if (channel.getProjectId() != null) { // Project channel
                // Assuming projectService has a method to count members
                // memberCount = projectService.countProjectMembers(channel.getProjectId());
                memberCount = 0; // Placeholder
            }
        }

        // 5. Map to DTO
        return ChannelDetailResponse.from(channel, teamName, projectName, channelMembers);
    }

    @Override
    @Transactional
    public ChannelResponse updateChannel(UUID channelId, UpdateChannelRequest request, UUID requesterId) {
        // 1. Find the channel
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channelId));

        // 2. Verify requester has permission to update the channel
        channelAuthorizationService.verifyCanUpdateChannel(channelId, requesterId);

        // 3. Update channel fields
        channel.setName(request.name());

        // 4. Save the updated channel
        Channel updatedChannel = channelRepository.save(channel);

        // 5. Map to DTO
        return ChannelResponse.from(updatedChannel);
    }

    @Override
    @Transactional
    public void deleteChannel(UUID channelId, UUID requesterId) {
        // 1. Find the channel
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channelId));

        // 2. Verify requester has permission to delete the channel
        channelAuthorizationService.verifyCanDeleteChannel(channelId, requesterId);

        // 3. Delete the channel
        channelRepository.delete(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelMemberResponse> getChannelMembers(UUID channelId, UUID requesterId) {
        // 1. Find the channel
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channelId));

        // 2. Verify requester has access to the channel
        verifyChannelAccess(channelId, requesterId);

        // 3. This endpoint is primarily for ad-hoc channels.
        // For team/project channels, members are derived from parent entities.
        if (channel.getTeamId() != null || channel.getProjectId() != null) {
            // Optionally throw BadRequestException if this endpoint is strictly for ad-hoc
            // throw new BadRequestException("This channel is not an ad-hoc channel. Members are derived from team/project.");
            return List.of(); // Return empty list for non-ad-hoc channels
        }

        // 4. Fetch channel members for ad-hoc channel
        List<ChannelMember> channelMembers = channelMemberRepository.findByIdChannelId(channelId);

        // 5. Map to DTOs
        return channelMembers.stream()
                .map(ChannelMemberResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChannelMemberResponse addMember(UUID channelId, AddChannelMemberRequest request, UUID requesterId) {
        // 1. Find the channel
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channelId));

        // 2. Ensure it's an ad-hoc channel
        if (channel.getTeamId() != null || channel.getProjectId() != null) {
            throw new BadRequestException("Members can only be added directly to ad-hoc channels.");
        }

        // 3. Verify requester has permission to add members
        channelAuthorizationService.verifyCanAddMember(channelId, requesterId);

        // 4. Find the user to be added
        User userToAdd = userService.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.userId()));

        // 5. Check for duplicate membership
        if (channelMemberRepository.existsByIdChannelIdAndIdUserId(channelId, request.userId())) {
            throw new ConflictException("User " + request.userId() + " is already a member of channel " + channelId);
        }

        // 6. Create and save new ChannelMember
        ChannelRole role = Optional.ofNullable(request.role()).orElse(ChannelRole.MEMBER);
        ChannelMember channelMember = ChannelMember.builder()
                .id(new ChannelMember.ChannelMemberId(channelId, request.userId()))
                .channel(channel)
                .user(userToAdd)
                .role(role.name())
                .build();

        ChannelMember savedChannelMember = channelMemberRepository.save(channelMember);

        // 7. Map to DTO
        return ChannelMemberResponse.from(savedChannelMember);
    }

    @Override
    @Transactional
    public void removeMember(UUID channelId, UUID userId, UUID requesterId) {
        // 1. Find the channel
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channelId));

        // 2. Ensure it's an ad-hoc channel
        if (channel.getTeamId() != null || channel.getProjectId() != null) {
            throw new BadRequestException("Members can only be removed directly from ad-hoc channels.");
        }

        // 3. Verify requester has permission to remove the member
        channelAuthorizationService.verifyCanRemoveMember(channelId, userId, requesterId);

        // 4. Find the channel member to remove
        ChannelMember channelMember = channelMemberRepository.findByIdChannelIdAndIdUserId(channelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " is not a member of channel " + channelId));

        // 5. Business Rule: Cannot remove the last OWNER
        if (channelMember.getRole().equals(ChannelRole.OWNER.name())) {
            long ownerCount = channelMemberRepository.findByIdChannelId(channelId).stream()
                    .filter(cm -> cm.getRole().equals(ChannelRole.OWNER.name()))
                    .count();
            if (ownerCount == 1) {
                throw new ConflictException("Cannot remove the last OWNER of the channel.");
            }
        }

        // 6. Delete the channel member
        channelMemberRepository.delete(channelMember);
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyChannelAccess(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channelId));

        if (channel.getTeamId() != null && channel.getProjectId() == null) {
            // Team channel
            teamAuthorizationService.verifyTeamMember(channel.getTeamId(), userId);
        } else if (channel.getProjectId() != null) {
            // Project channel
            projectAuthorizationService.verifyProjectMember(channel.getProjectId(), userId);
        } else if (channel.getTeamId() == null && channel.getProjectId() == null) {
            // Ad-hoc channel
            if (!channelMemberRepository.existsByIdChannelIdAndIdUserId(channelId, userId)) {
                throw new ForbiddenException("You are not a member of this ad-hoc channel.");
            }
        } else {
            // This case should ideally not be reached if channel creation validation is correct
            throw new BadRequestException("Invalid channel type configuration for channel ID: " + channelId);
        }
    }
}
