package tn.IIT.mentorat_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private Long id;
    private LocalDateTime dateCreation;
    private Long demandeId;

    // Participants
    private Long etudiantId;
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantPhoto;

    private Long alumniId;
    private String alumniNom;
    private String alumniPrenom;
    private String alumniPhoto;

    // Résumé
    private long nombreMessages;
    private long messagesNonLus;
    private MessageResponse dernierMessage;

    private List<MessageResponse> messages; // null si liste condensée
}
