package tn.IIT.mentorat_platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(nullable = false)
    private LocalDateTime dateEnvoi;

    @Builder.Default
    private Boolean lu = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediteur_id", nullable = false)
    private Utilisateur expediteur;

    private String fileUrl;
    private String fileType; // IMAGE, FILE, AUDIO

    @PrePersist
    public void prePersist() {
        this.dateEnvoi = LocalDateTime.now();
        this.lu = false;
    }
}
