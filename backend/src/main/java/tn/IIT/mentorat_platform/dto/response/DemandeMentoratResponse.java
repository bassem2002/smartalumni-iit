package tn.IIT.mentorat_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.IIT.mentorat_platform.enums.StatutDemande;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandeMentoratResponse {
    private Long id;
    private StatutDemande statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateMiseAJour;
    private String message;

    // Etudiant émetteur
    private Long etudiantId;
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantPhoto;

    // Alumni récepteur
    private Long alumniId;
    private String alumniNom;
    private String alumniPrenom;
    private String alumniPhoto;
    private String alumniPoste;

    // Conversation liée (si existante)
    private Long conversationId;
}
