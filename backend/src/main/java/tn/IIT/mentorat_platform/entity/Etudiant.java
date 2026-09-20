package tn.IIT.mentorat_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("ETUDIANT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Etudiant extends Utilisateur {

    private String niveauEtude;

    private String filiere;

    private String specialite;

    private Integer anneePromotion;

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DemandeMentorat> demandesEnvoyees = new ArrayList<>();

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RecommandationIA> recommandations = new ArrayList<>();
}
