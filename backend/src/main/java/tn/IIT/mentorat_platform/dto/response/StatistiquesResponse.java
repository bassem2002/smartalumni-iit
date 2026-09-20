package tn.IIT.mentorat_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesResponse {
    // Utilisateurs
    private long totalUtilisateurs;
    private long totalEtudiants;
    private long totalAlumni;

    // Mentorat
    private long totalDemandesEnAttente;
    private long totalDemandesAcceptees;
    private long totalDemandesRefusees;
    private long totalMentorsDisponibles;

    // Contenu
    private long totalConversations;
    private long totalMessages;
    private long totalTitresEnAttente;
    private long totalCVTelecharges;

    // Repartition
    private java.util.Map<String, Long> alumniParSecteur;
}
