package tn.IIT.mentorat_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.IIT.mentorat_platform.entity.Conversation;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByDemandeId(Long demandeId);

    @Query("""
            SELECT c FROM Conversation c
            WHERE c.demande.etudiant.id = :userId
               OR c.demande.alumni.id = :userId
            ORDER BY c.dateCreation DESC
            """)
    List<Conversation> findAllByUserId(Long userId);
}
