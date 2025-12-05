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
    private final AuthorizationService authService;

    public ChannelDTO createChannel(ChannelRequest channelRequest, UUID userId) {
        Team team = teamRepository.findById(channelRequest.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + channelRequest.getTeamId()));
        Project project = null;
        if (channelRequest.getProjectId() != null) {
            project = projectRepository.findById(channelRequest.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + channelRequest.getProjectId()));
            authService.checkProjectAdminAccess(team.getId(), project.getId(), userId);
        } else {
            authService.checkIfUserIsAdminInTeam(team.getId(), userId);
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

    public ChannelDTO getChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));
        authService.checkChannelAccess(channel, userId);
        return AppMapper.convertToChannelDto(channel);
    }

    public List<ChannelDTO> getChannelsForTeam(UUID teamId, UUID userId) {
        authService.checkTeamAccess(teamId, userId);
        List<Channel> channels = channelRepository.findByTeamIdAndProjectIdIsNull(teamId);
        return channels.stream().map(AppMapper::convertToChannelDto).toList();
    }

    public List<ChannelDTO> getChannelsForProject(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        authService.checkProjectAccess(project.getTeam().getId(), projectId, userId);
        List<Channel> channels = channelRepository.findByProjectId(projectId);
        return channels.stream().map(AppMapper::convertToChannelDto).toList();
    }

    public ChannelDTO updateChannel(UUID channelId, ChannelRequest channelRequest, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));
        authService.checkChannelAdminAccess(channel, userId);

        channel.setName(channelRequest.getName());
        channel.setIsPrivate(channelRequest.getIsPrivate());
        channel.setDescription(channelRequest.getDescription());
        channelRepository.save(channel);
        return AppMapper.convertToChannelDto(channel);
    }

    public void deleteChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));
        authService.checkChannelAdminAccess(channel, userId);
        channelRepository.delete(channel);
    }
}
