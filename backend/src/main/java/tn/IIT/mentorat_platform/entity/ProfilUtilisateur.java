package tn.IIT.mentorat_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "profils_utilisateurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfilUtilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String competences;

    @Column(columnDefinition = "TEXT")
    private String experiences;

    private String emailProfessionnel;
    
    private String linkedinUrl;
    
    private String photo; // URL ou base64 de la photo de profil

    @OneToMany(mappedBy = "profil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TitreProfessionnel> titres;

    private LocalDateTime derniereMiseAJour;

    @Enumerated(EnumType.STRING)
    private tn.IIT.mentorat_platform.enums.TypeProfil type;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        derniereMiseAJour = LocalDateTime.now();
    }
}
