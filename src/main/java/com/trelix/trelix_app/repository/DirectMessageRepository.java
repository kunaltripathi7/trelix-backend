package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessage, UUID> {
    
    @Query("SELECT dm FROM DirectMessage dm WHERE " +
           "(dm.user1.id = :user1Id AND dm.user2.id = :user2Id) OR " +
           "(dm.user1.id = :user2Id AND dm.user2.id = :user1Id)")
    Optional<DirectMessage> findByUsers(@Param("user1Id") UUID user1Id, @Param("user2Id") UUID user2Id);
    
    @Query("SELECT dm FROM DirectMessage dm WHERE dm.user1.id = :userId OR dm.user2.id = :userId")
    List<DirectMessage> findByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT dm, m.content, m.createdAt FROM DirectMessage dm " +
           "LEFT JOIN dm.messages m ON m.id = (SELECT MAX(m2.id) FROM Message m2 WHERE m2.directMessage.id = dm.id) " +
           "WHERE dm.user1.id = :userId OR dm.user2.id = :userId " +
           "ORDER BY m.createdAt DESC NULLS LAST")
    List<Object[]> findByUserIdWithLastMessage(@Param("userId") UUID userId);
}
