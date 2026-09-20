package tn.IIT.mentorat_platform.dto.request;

import lombok.Data;

@Data
public class AlumniFilterRequest {
    private String secteur;
    private String posteActuel;
    private String pays;
    private Boolean disponibleMentorat;
    private String keyword; // Recherche libre dans nom/prénom/bio
    private int page = 0;
    private int size = 10;
    private String sortBy = "nom";
}
