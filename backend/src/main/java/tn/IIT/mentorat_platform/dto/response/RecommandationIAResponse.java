package tn.IIT.mentorat_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommandationIAResponse {
    private Long id;
    private Double scoreMatching;
    private String raison;
    private LocalDateTime dateGeneration;

    // Alumni recommandé
    private Long alumniId;
    private String alumniNom;
    private String alumniPrenom;
    private String alumniPhoto;
    private String secteur;
    private String posteActuel;
    private String entreprise;
    private String pays;
    private String competences;
}
