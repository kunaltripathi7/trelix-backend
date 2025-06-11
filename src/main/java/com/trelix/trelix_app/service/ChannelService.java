package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.ChannelDTO;
import com.trelix.trelix_app.dto.ChannelRequest;
import com.trelix.trelix_app.entity.Channel;
import com.trelix.trelix_app.entity.Project;
import com.trelix.trelix_app.entity.Team;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.ChannelRepository;
import com.trelix.trelix_app.repository.ProjectRepository;
import com.trelix.trelix_app.repository.TeamRepository;
import com.trelix.trelix_app.util.AppMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final AuthorizationService authorizationService;

    public ChannelDTO createChannel(UUID teamId, UUID projectId, ChannelRequest channelRequest) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        Project project = null;
        if (projectId != null) {
        project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        }
        Channel channel = Channel.builder()
                .team(team)
                .project(project)
                .name(channelRequest.getName())
                .isPrivate(channelRequest.getIsPrivate())
                .description(channelRequest.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
        channelRepository.save(channel);
        return AppMapper.convertToChannelDto(channel);
    }

    public ChannelDTO getChannel(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));
        return AppMapper.convertToChannelDto(channel);
    }

    public List<ChannelDTO> getChannelsForTeam(UUID teamId) {
        List<Channel> channels = channelRepository.findByTeamId(teamId);
        return channels.stream().map(AppMapper::convertToChannelDto)
                .toList();
    }

    public List<ChannelDTO> getChannelsForProject(UUID projectId) {
        List<Channel> channels = channelRepository.findByProjectId(projectId);
        return channels.stream().map(AppMapper::convertToChannelDto)
                .toList();
    }

    public ChannelDTO updateChannel(UUID channelId, ChannelRequest channelRequest) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));
        channel.setName(channelRequest.getName());
        channel.setIsPrivate(channelRequest.getIsPrivate());
        channel.setDescription(channelRequest.getDescription());
        channelRepository.save(channel);
        return AppMapper.convertToChannelDto(channel);
    }

    public void deleteChannel(UUID teamId, UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));
        if (channel.getProject() != null) {
            authorizationService.checkProjectAdminAccess(teamId, channel.getProject().getId(), userId);
        }
        else {
            authorizationService.checkIfUserIsAdminInTeam(teamId, userId);
        }
        channelRepository.delete(channel);
    }
}
