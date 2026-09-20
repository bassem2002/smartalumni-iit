package tn.IIT.mentorat_platform.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("ADMIN")
@NoArgsConstructor
@SuperBuilder
public class Administrateur extends Utilisateur {
    // Hérite uniquement de Utilisateur
    // Les fonctionnalités d'admin sont gérées via les services et le rôle ROLE_ADMIN
}
