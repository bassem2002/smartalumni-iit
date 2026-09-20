package tn.IIT.mentorat_platform.dto.response;

import lombok.*;
import java.util.List;
import tn.IIT.mentorat_platform.dto.response.TitreProfessionnelResponse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfilResponse {
    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private String typeUser; // Ajouté pour le badge du profil
    private String bio;
    private String photo;
    private String competences;
    private String experiences;
    private String emailProfessionnel;
    private String linkedinUrl;
    private List<TitreProfessionnelResponse> titres;
    
    // Champs spécifiques Alumni
    private String secteur;
    private String posteActuel;
    private String entreprise;
    private String pays;
    private Boolean disponibleMentorat;
    private String demandeStatut; // Changé en String pour plus de souplesse côté JS
    
    // Champs spécifiques Etudiant
    private String niveauEtude;
    private String filiere;
    private String specialite;
    private Integer anneePromotion;
}
