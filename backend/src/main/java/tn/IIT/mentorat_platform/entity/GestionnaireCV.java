package tn.IIT.mentorat_platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "gestionnaires_cv")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GestionnaireCV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomFichier;

    /** Chemin local : uploads/cv/userId_filename.pdf */
    @Column(nullable = false)
    private String cheminStockage;

    private LocalDateTime dateDepot;

    /** Texte brut extrait par Apache Tika */
    @Column(columnDefinition = "TEXT")
    private String contenuBrut;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profil_id")
    private ProfilUtilisateur profil;

    private String posteDetecte;
    private String entrepriseDetectee;
    private String secteurDetecte;
    private String paysDetecte;

    @Column(columnDefinition = "TEXT")
    private String competencesDetectees;

    @Column(columnDefinition = "TEXT")
    private String experiencesDetectees;

    @Column(columnDefinition = "TEXT")
    private String formationsDetectees;

    @Column(columnDefinition = "TEXT")
    private String certificationsDetectees;
}
