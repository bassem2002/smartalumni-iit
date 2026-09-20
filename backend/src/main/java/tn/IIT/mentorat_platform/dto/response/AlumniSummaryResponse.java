package tn.IIT.mentorat_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumniSummaryResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String photo;
    private String bio;
    private String secteur;
    private String posteActuel;
    private String entreprise;
    private String pays;
    private Boolean disponibleMentorat;
    private String competences;
    // Statistiques rapides
    private long nombreDemandesRecues;
}
