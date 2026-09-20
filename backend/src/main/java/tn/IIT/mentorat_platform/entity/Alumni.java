package tn.IIT.mentorat_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import tn.IIT.mentorat_platform.enums.StatutDemandeMentorat;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("ALUMNI")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Alumni extends Utilisateur {

    private String secteur;

    private String posteActuel;

    private String entreprise;

    private String pays;

    @Builder.Default
    private Boolean disponibleMentorat = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutDemandeMentorat demandeStatut = StatutDemandeMentorat.AUCUNE;

    private String filiere;

    private String specialite;

    private Integer anneePromotion;

    @OneToMany(mappedBy = "alumni", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DemandeMentorat> demandesRecues = new ArrayList<>();

    @OneToMany(mappedBy = "alumni", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RecommandationIA> recommandations = new ArrayList<>();
}
