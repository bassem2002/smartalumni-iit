package tn.IIT.mentorat_platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommandations_ia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommandationIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Score de compatibilité entre 0 et 1 (calculé par le moteur de scoring métier) */
    @Column(nullable = false)
    private Double scoreMatching;

    /** Explication générée par l'IA */
    @Column(columnDefinition = "TEXT")
    private String raison;

    @Column(nullable = false)
    private LocalDateTime dateGeneration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumni_id", nullable = false)
    private Alumni alumni;

    @PrePersist
    public void prePersist() {
        this.dateGeneration = LocalDateTime.now();
    }
}
