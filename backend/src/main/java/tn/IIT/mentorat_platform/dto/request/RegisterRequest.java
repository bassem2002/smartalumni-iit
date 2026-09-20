package tn.IIT.mentorat_platform.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tn.IIT.mentorat_platform.enums.TypeProfil;

@Data
public class RegisterRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@iit\\.ens\\.tn$", message = "L'email doit se terminer par @iit.ens.tn")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;

    @NotNull(message = "Le type de profil est obligatoire (ETUDIANT ou ALUMNI)")
    private TypeProfil typeProfil;

    // Champs communs
    @NotBlank(message = "La filière est obligatoire")
    private String filiere;

    @NotBlank(message = "La spécialité est obligatoire")
    private String specialite;

    // Champs spécifiques ETUDIANT
    private String niveauEtude;
    private Integer anneePromotion;

    // Champs spécifiques ALUMNI
    private String secteur;
    private String posteActuel;
    private String entreprise;
    private String pays;
}
