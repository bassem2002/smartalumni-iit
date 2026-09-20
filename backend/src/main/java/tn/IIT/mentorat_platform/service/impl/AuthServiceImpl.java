package tn.IIT.mentorat_platform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tn.IIT.mentorat_platform.dto.request.LoginRequest;
import tn.IIT.mentorat_platform.dto.request.RegisterRequest;
import tn.IIT.mentorat_platform.dto.response.AuthResponse;
import tn.IIT.mentorat_platform.entity.*;
import tn.IIT.mentorat_platform.enums.RoleEnum;
import tn.IIT.mentorat_platform.enums.StatutCompte;
import tn.IIT.mentorat_platform.enums.TypeProfil;
import tn.IIT.mentorat_platform.exception.BusinessException;
import tn.IIT.mentorat_platform.exception.ResourceNotFoundException;
import tn.IIT.mentorat_platform.repository.GestionnaireCVRepository;
import tn.IIT.mentorat_platform.repository.ProfilUtilisateurRepository;
import tn.IIT.mentorat_platform.repository.RoleRepository;
import tn.IIT.mentorat_platform.repository.UtilisateurRepository;
import tn.IIT.mentorat_platform.security.CustomUserDetailsService;
import tn.IIT.mentorat_platform.security.JwtTokenProvider;
import tn.IIT.mentorat_platform.service.AuthService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

        private final UtilisateurRepository utilisateurRepository;
        private final RoleRepository roleRepository;
        private final ProfilUtilisateurRepository profilUtilisateurRepository;
        private final GestionnaireCVRepository cvRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtTokenProvider jwtTokenProvider;
        private final AuthenticationManager authenticationManager;
        private final CustomUserDetailsService userDetailsService;

        @Value("${app.upload.cv-dir:uploads/cv/}")
        private String cvUploadDir;

        @Override
        @Transactional
        public AuthResponse register(RegisterRequest request, MultipartFile cvFile) {
                // Validate email domain
                if (!request.getEmail().toLowerCase().endsWith("@iit.ens.tn")) {
                        throw new BusinessException("L'email doit se terminer par @iit.ens.tn");
                }

                // Validate CV file
                if (cvFile == null || cvFile.isEmpty()) {
                        throw new BusinessException("Le CV est obligatoire");
                }

                if (utilisateurRepository.existsByEmail(request.getEmail())) {
                        throw new BusinessException("Cet email est déjà utilisé");
                }

                // 1. Assigner le bon rôle (Gestion des 3 cas)
                RoleEnum roleEnum;
                if (request.getTypeProfil() == TypeProfil.ADMIN) {
                        roleEnum = RoleEnum.ROLE_ADMIN;
                } else if (request.getTypeProfil() == TypeProfil.ALUMNI) {
                        roleEnum = RoleEnum.ROLE_ALUMNI;
                } else {
                        roleEnum = RoleEnum.ROLE_ETUDIANT;
                }

                Role role = roleRepository.findByLibelle(roleEnum.name())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Rôle " + roleEnum.name() + " non trouvé en base"));

                // 2. Créer l'entité appropriée
                Utilisateur utilisateur;

                if (request.getTypeProfil() == TypeProfil.ADMIN) {
                        utilisateur = Administrateur.builder()
                                        .nom(request.getNom())
                                        .prenom(request.getPrenom())
                                        .email(request.getEmail())
                                        .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                                        .dateInscription(LocalDateTime.now())
                                        .statutCompte(StatutCompte.ACTIF)
                                        .role(role)
                                        .build();
                } else if (request.getTypeProfil() == TypeProfil.ALUMNI) {
                        utilisateur = Alumni.builder()
                                        .nom(request.getNom())
                                        .prenom(request.getPrenom())
                                        .email(request.getEmail())
                                        .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                                        .dateInscription(LocalDateTime.now())
                                        .statutCompte(StatutCompte.ACTIF)
                                        .role(role)
                                        .secteur(request.getSecteur())
                                        .posteActuel(request.getPosteActuel())
                                        .entreprise(request.getEntreprise())
                                        .pays(request.getPays())
                                        .disponibleMentorat(false)
                                        .build();
                } else {
                        utilisateur = Etudiant.builder()
                                        .nom(request.getNom())
                                        .prenom(request.getPrenom())
                                        .email(request.getEmail())
                                        .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                                        .dateInscription(LocalDateTime.now())
                                        .statutCompte(StatutCompte.ACTIF)
                                        .role(role)
                                        .niveauEtude(request.getNiveauEtude())
                                        .filiere(request.getFiliere())
                                        .specialite(request.getSpecialite())
                                        .anneePromotion(request.getAnneePromotion())
                                        .build();
                }

                utilisateurRepository.save(utilisateur);

                // 3. Créer le profil associé (OneToOne)
                ProfilUtilisateur profil = ProfilUtilisateur.builder()
                                .type(request.getTypeProfil())
                                .utilisateur(utilisateur)
                                .derniereMiseAJour(LocalDateTime.now())
                                .build();

                profilUtilisateurRepository.save(profil);

                // 4. Sauvegarder le CV
                try {
                        Path uploadPath = Paths.get(cvUploadDir);
                        if (!Files.exists(uploadPath)) {
                                Files.createDirectories(uploadPath);
                        }

                        String filename = utilisateur.getId() + "_" + System.currentTimeMillis() + "_" + cvFile.getOriginalFilename();
                        Path filePath = uploadPath.resolve(filename);
                        Files.copy(cvFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        // Extraire le texte avec Apache Tika
                        String extractedText = "";
                        try {
                                Tika tika = new Tika();
                                extractedText = tika.parseToString(cvFile.getInputStream());
                        } catch (Exception e) {
                                log.warn("Impossible d'extraire le texte du CV: {}", e.getMessage());
                        }

                        GestionnaireCV cv = GestionnaireCV.builder()
                                        .utilisateur(utilisateur)
                                        .profil(profil)
                                        .nomFichier(cvFile.getOriginalFilename())
                                        .cheminStockage(filePath.toString())
                                        .dateDepot(LocalDateTime.now())
                                        .contenuBrut(extractedText)
                                        .build();

                        cvRepository.save(cv);
                } catch (Exception e) {
                        log.error("Erreur lors de la sauvegarde du CV", e);
                        throw new BusinessException("Erreur lors de la sauvegarde du CV: " + e.getMessage());
                }

                // 5. Générer les tokens JWT
                String token = jwtTokenProvider.generateToken(utilisateur);
                String refreshToken = jwtTokenProvider.generateRefreshToken(utilisateur);

                return AuthResponse.builder()
                                .token(token)
                                .refreshToken(refreshToken)
                                .userId(utilisateur.getId())
                                .email(utilisateur.getEmail())
                                .nom(utilisateur.getNom())
                                .prenom(utilisateur.getPrenom())
                                .role(role.getLibelle())
                                .typeProfil(request.getTypeProfil().name())
                                .build();
        }

        @Override
        public AuthResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse()));

                UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
                Utilisateur utilisateur = (Utilisateur) userDetails;

                String token = jwtTokenProvider.generateToken(userDetails);
                String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

                String typeProfil = (utilisateur instanceof Alumni) ? "ALUMNI"
                                : (utilisateur instanceof Etudiant ? "ETUDIANT" : "ADMIN");

                return AuthResponse.builder()
                                .token(token)
                                .refreshToken(refreshToken)
                                .userId(utilisateur.getId())
                                .email(utilisateur.getEmail())
                                .nom(utilisateur.getNom())
                                .prenom(utilisateur.getPrenom())
                                .role(utilisateur.getRole().getLibelle())
                                .typeProfil(typeProfil)
                                .build();
        }
}
