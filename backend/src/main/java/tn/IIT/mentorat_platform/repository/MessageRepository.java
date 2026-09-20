package tn.IIT.mentorat_platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.IIT.mentorat_platform.entity.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByConversationIdOrderByDateEnvoiAsc(Long conversationId, Pageable pageable);
    List<Message> findByConversationIdAndLuFalseAndExpediteurIdNot(Long conversationId, Long userId);
    long countByConversationIdAndLuFalse(Long conversationId);
}
