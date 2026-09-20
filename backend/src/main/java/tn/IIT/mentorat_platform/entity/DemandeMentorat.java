package tn.IIT.mentorat_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.IIT.mentorat_platform.enums.StatutDemande;

import java.time.LocalDateTime;

@Entity
@Table(name = "demandes_mentorat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeMentorat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    private LocalDateTime dateMiseAJour;

    @Column(columnDefinition = "TEXT")
    private String message; // Message d'introduction de l'étudiant

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumni_id", nullable = false)
    private Alumni alumni;

    @OneToOne(mappedBy = "demande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Conversation conversation;

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        this.statut = StatutDemande.EN_ATTENTE;
    }

    @PreUpdate
    public void preUpdate() {
        this.dateMiseAJour = LocalDateTime.now();
    }
}
