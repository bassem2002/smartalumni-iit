package tn.IIT.mentorat_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.IIT.mentorat_platform.enums.StatutVerification;

import java.time.LocalDate;

@Entity
@Table(name = "titres_professionnels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TitreProfessionnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String intitule;

    private String organismeDelivrant;

    private LocalDate dateObtention;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutVerification statutVerification = StatutVerification.EN_ATTENTE;

    /** Chemin local vers le document de preuve (ex: uploads/titres/123_diplome.pdf) */
    private String preuveDocumentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profil_id", nullable = false)
    private ProfilUtilisateur profil;
}
