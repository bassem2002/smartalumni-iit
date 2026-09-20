package tn.IIT.mentorat_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.IIT.mentorat_platform.enums.StatutCompte;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private StatutCompte statutCompte;
    private LocalDateTime dateInscription;
    private String typeProfil;
    
    // Extended properties for frontend filtering
    private String filiere;
    private String specialite;
    private String posteActuel;
    private String entreprise;
    private String secteur;
}
