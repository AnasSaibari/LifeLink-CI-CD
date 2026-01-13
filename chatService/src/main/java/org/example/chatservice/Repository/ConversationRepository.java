package org.example.chatservice.Repository;

import org.example.chatservice.Entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findBySenderIdOrReceiverId(Long senderId, Long receiverId);
    
    // Trouver une conversation existante entre deux participants (dans les deux sens)
    Optional<Conversation> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    // Query pour trouver une conversation existante dans les deux sens
    @Query("SELECT c FROM Conversation c WHERE (c.senderId = :userId1 AND c.receiverId = :userId2) OR (c.senderId = :userId2 AND c.receiverId = :userId1)")
    Optional<Conversation> findExistingConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
