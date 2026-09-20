package tn.IIT.mentorat_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GestionnaireCVResponse {
    private Long id;
    private String nomFichier;
    private String cheminStockage;
    private LocalDateTime dateDepot;
    private boolean contenuExtrait;

    private String posteDetecte;
    private String entrepriseDetectee;
    private String secteurDetecte;
    private String paysDetecte;

    @Builder.Default
    private List<String> competencesDetectees = new ArrayList<>();

    @Builder.Default
    private List<String> experiencesDetectees = new ArrayList<>();

    @Builder.Default
    private List<String> formationsDetectees = new ArrayList<>();

    @Builder.Default
    private List<String> certificationsDetectees = new ArrayList<>();
}
