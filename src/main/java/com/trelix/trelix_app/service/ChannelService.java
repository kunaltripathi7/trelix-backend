package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.AddChannelMemberRequest;
import com.trelix.trelix_app.dto.ChannelDetailResponse;
import com.trelix.trelix_app.dto.ChannelMemberResponse;
import com.trelix.trelix_app.dto.ChannelResponse;
import com.trelix.trelix_app.dto.CreateChannelRequest;
import com.trelix.trelix_app.dto.UpdateChannelRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse createChannel(CreateChannelRequest request, UUID creatorId);

    List<ChannelResponse> getChannels(UUID teamId, UUID projectId, String type, UUID requesterId);

    ChannelDetailResponse getChannelById(UUID channelId, UUID requesterId);

    ChannelResponse updateChannel(UUID channelId, UpdateChannelRequest request, UUID requesterId);

    void deleteChannel(UUID channelId, UUID requesterId);

    List<ChannelMemberResponse> getChannelMembers(UUID channelId, UUID requesterId);

    ChannelMemberResponse addMember(UUID channelId, AddChannelMemberRequest request, UUID requesterId);

    void removeMember(UUID channelId, UUID userId, UUID requesterId);

}
