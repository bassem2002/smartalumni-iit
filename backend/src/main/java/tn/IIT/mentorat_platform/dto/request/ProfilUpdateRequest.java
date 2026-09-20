package tn.IIT.mentorat_platform.dto.request;

import lombok.Data;

@Data
public class ProfilUpdateRequest {

    private String bio;
    private String competences; // Format JSON ou CSV
    private String experiences; // Format JSON
    private String photo;
    private String linkedinUrl;
    private String emailProfessionnel;

    // Champs Alumni
    private String secteur;
    private String posteActuel;
    private String entreprise;
    private String pays;

    // Champs Etudiant
    private String niveauEtude;
    private String filiere;
    private String specialite;
    private Integer anneePromotion;
}
