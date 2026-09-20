package tn.IIT.mentorat_platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_id", nullable = false, unique = true)
    private DemandeMentorat demande;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
    }
}
