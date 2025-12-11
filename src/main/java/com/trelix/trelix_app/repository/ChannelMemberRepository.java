//package com.trelix.trelix_app.repository;
//
//import com.trelix.trelix_app.entity.ChannelMember;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Repository
//public interface ChannelMemberRepository extends JpaRepository<ChannelMember, ChannelMember.ChannelMemberId> {
//
//    List<ChannelMember> findByIdChannelId(UUID channelId);
//
//    Optional<ChannelMember> findByIdChannelIdAndIdUserId(UUID channelId, UUID userId);
//
//    boolean existsByIdChannelIdAndIdUserId(UUID channelId, UUID userId);
//
//    long countByIdChannelId(UUID channelId);
//}
