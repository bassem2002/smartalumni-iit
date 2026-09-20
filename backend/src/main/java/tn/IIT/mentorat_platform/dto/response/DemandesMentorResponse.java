package tn.IIT.mentorat_platform.dto.response;

import lombok.Builder;
import lombok.Data;
import tn.IIT.mentorat_platform.enums.StatutDemandeMentorat;

@Data
@Builder
public class DemandesMentorResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String secteur;
    private String posteActuel;
    private String entreprise;
    private String pays;
    private StatutDemandeMentorat demandeStatut;
}
