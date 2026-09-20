package tn.IIT.mentorat_platform.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.IIT.mentorat_platform.entity.Administrateur;
import tn.IIT.mentorat_platform.entity.Role;
import tn.IIT.mentorat_platform.enums.RoleEnum;
import tn.IIT.mentorat_platform.enums.StatutCompte;
import tn.IIT.mentorat_platform.repository.RoleRepository;
import tn.IIT.mentorat_platform.repository.UtilisateurRepository;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("ALTER TABLE profils_utilisateurs DROP CONSTRAINT IF EXISTS profils_utilisateurs_type_check");
            log.info("Constraint profils_utilisateurs_type_check dropped successfully.");
        } catch (Exception e) {
            log.error("Could not drop constraint: ", e);
        }

        // 1. Vérifier et insérer les rôles
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleRepository.findByLibelle(roleEnum.name()).isEmpty()) {
                Role role = Role.builder()
                        .libelle(roleEnum.name())
                        .description("Rôle " + roleEnum.name())
                        .build();
                roleRepository.save(role);
                log.info("Rôle créé lors de l'initialisation : {}", roleEnum.name());
            }
        }

        // 2. Créer un compte Administrateur par défaut
        String adminEmail = "admin@mentorat.tn";
        if (!utilisateurRepository.existsByEmail(adminEmail)) {
            Role adminRole = roleRepository.findByLibelle(RoleEnum.ROLE_ADMIN.name())
                    .orElseThrow(() -> new RuntimeException("Rôle ADMIN introuvable"));

            Administrateur admin = Administrateur.builder()
                    .nom("Admin")
                    .prenom("Super")
                    .email(adminEmail)
                    .motDePasse(passwordEncoder.encode("admin123"))
                    .dateInscription(LocalDateTime.now())
                    .statutCompte(StatutCompte.ACTIF)
                    .role(adminRole)
                    .build();

            utilisateurRepository.save(admin);
            log.info("Compte Administrateur par défaut créé (Email: {})", adminEmail);
        } else {
            log.info("Le compte Administrateur par défaut existe déjà (Email: {})", adminEmail);
        }
    }
}
