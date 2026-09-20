package tn.IIT.mentorat_platform.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.IIT.mentorat_platform.entity.Administrateur;
import tn.IIT.mentorat_platform.entity.Role;
import tn.IIT.mentorat_platform.entity.Utilisateur;
import tn.IIT.mentorat_platform.enums.StatutCompte;
import tn.IIT.mentorat_platform.repository.RoleRepository;
import tn.IIT.mentorat_platform.repository.UtilisateurRepository;
import tn.IIT.mentorat_platform.service.FormationScraperService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FormationScraperService formationScraperService;

    public DataSeeder(UtilisateurRepository utilisateurRepository, 
                      RoleRepository roleRepository, 
                      PasswordEncoder passwordEncoder,
                      FormationScraperService formationScraperService) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.formationScraperService = formationScraperService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Create ROLE_ADMIN and ROLE_ALUMNI
        Role adminRole = roleRepository.findByLibelle("ROLE_ADMIN").orElseGet(() -> {
            Role r = new Role();
            r.setLibelle("ROLE_ADMIN");
            r.setDescription("Administrateur du système");
            return roleRepository.save(r);
        });

        Role alumniRole = roleRepository.findByLibelle("ROLE_ALUMNI").orElseGet(() -> {
            Role r = new Role();
            r.setLibelle("ROLE_ALUMNI");
            r.setDescription("Ancien étudiant");
            return roleRepository.save(r);
        });

        // 2. Create/Ensure Admin user
        String adminEmail = "iit.allumni@iit.ens.tn";
        if (utilisateurRepository.findByEmail(adminEmail).isEmpty()) {
            Administrateur admin = new Administrateur();
            admin.setNom("IIT");
            admin.setPrenom("Admin");
            admin.setEmail(adminEmail);
            admin.setMotDePasse(passwordEncoder.encode("smartallumni"));
            admin.setDateInscription(LocalDateTime.now());
            admin.setStatutCompte(StatutCompte.ACTIF);
            admin.setRole(adminRole);

            utilisateurRepository.save(admin);
            System.out.println("✅ Administrateur créé avec succès: " + adminEmail);
        }
    }
}
