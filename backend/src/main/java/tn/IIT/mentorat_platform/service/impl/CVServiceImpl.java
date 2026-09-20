package tn.IIT.mentorat_platform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tn.IIT.mentorat_platform.dto.response.AiCvExtractionResponse;
import tn.IIT.mentorat_platform.dto.response.GestionnaireCVResponse;
import tn.IIT.mentorat_platform.entity.Alumni;
import tn.IIT.mentorat_platform.entity.GestionnaireCV;
import tn.IIT.mentorat_platform.entity.ProfilUtilisateur;
import tn.IIT.mentorat_platform.entity.Utilisateur;
import tn.IIT.mentorat_platform.exception.BusinessException;
import tn.IIT.mentorat_platform.exception.ResourceNotFoundException;
import tn.IIT.mentorat_platform.repository.GestionnaireCVRepository;
import tn.IIT.mentorat_platform.repository.ProfilUtilisateurRepository;
import tn.IIT.mentorat_platform.repository.UtilisateurRepository;
import tn.IIT.mentorat_platform.service.AiCvExtractionService;
import tn.IIT.mentorat_platform.service.CVService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CVServiceImpl implements CVService {

    private final GestionnaireCVRepository cvRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ProfilUtilisateurRepository profilUtilisateurRepository;
    private final AiCvExtractionService aiCvExtractionService;

    @Value("${app.upload.cv-dir:uploads/cv/}")
    private String uploadDir;

    @Override
    @Transactional
    public GestionnaireCVResponse uploadCV(String email, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("Le fichier est vide");
        }

        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        ProfilUtilisateur profil = profilUtilisateurRepository.findByUtilisateurId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profil non trouvé"));

        try {
            // 1. Créer le dossier s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. Sauvegarder le fichier localement
            String filename = user.getId() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 3. Extraire le texte avec Apache Tika
            Tika tika = new Tika();
            String extractedText = tika.parseToString(file.getInputStream());

            // 4. Mettre à jour ou créer l'entité GestionnaireCV
            Optional<GestionnaireCV> existingCV = cvRepository.findByUtilisateurId(user.getId());
            GestionnaireCV cv = existingCV.orElse(new GestionnaireCV());

            // Si un ancien CV existait, on supprime l'ancien fichier
            if (existingCV.isPresent() && cv.getCheminStockage() != null) {
                try {
                    Files.deleteIfExists(Paths.get(cv.getCheminStockage()));
                } catch (IOException e) {
                    log.warn("Impossible de supprimer l'ancien CV: {}", e.getMessage());
                }
            }

            cv.setUtilisateur(user);
            cv.setProfil(profil);
            cv.setNomFichier(file.getOriginalFilename());
            cv.setCheminStockage(filePath.toString());
            cv.setDateDepot(LocalDateTime.now());
            cv.setContenuBrut(extractedText);

            // Réinitialiser les champs détectés lors d'un nouvel upload
            cv.setPosteDetecte(null);
            cv.setEntrepriseDetectee(null);
            cv.setSecteurDetecte(null);
            cv.setPaysDetecte(null);
            cv.setCompetencesDetectees(null);
            cv.setExperiencesDetectees(null);
            cv.setFormationsDetectees(null);
            cv.setCertificationsDetectees(null);

            cv = cvRepository.save(cv);
            return mapToResponse(cv);

        } catch (Exception e) {
            log.error("Erreur lors de l'upload du CV", e);
            throw new BusinessException("Erreur lors du traitement du fichier: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GestionnaireCVResponse getCV(Long cvId, String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        GestionnaireCV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new ResourceNotFoundException("CV non trouvé"));

        // Sécurité : vérifier que le CV appartient bien à l'utilisateur
        if (!cv.getUtilisateur().getId().equals(user.getId())) {
            throw new BusinessException("Vous n'êtes pas autorisé à accéder à ce CV");
        }

        return mapToResponse(cv);
    }

    @Override
    @Transactional
    public GestionnaireCVResponse analyserCV(Long cvId, String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        GestionnaireCV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new ResourceNotFoundException("CV non trouvé"));

        if (!cv.getUtilisateur().getId().equals(user.getId())) {
            throw new BusinessException("Vous n'êtes pas autorisé à analyser ce CV");
        }

        String extractedText = cv.getContenuBrut();
        if (extractedText == null || extractedText.isBlank()) {
            throw new BusinessException("Le texte du CV n'a pas pu être extrait. Veuillez uploader à nouveau.");
        }

        // 5. Appeler l'analyse IA ou fallback local
        try {
            AiCvExtractionResponse aiResponse = aiCvExtractionService.extractFromText(extractedText);
            if (aiResponse != null) {
                cv.setPosteDetecte(aiResponse.getPosteDetecte());
                cv.setEntrepriseDetectee(aiResponse.getEntrepriseDetectee());
                cv.setSecteurDetecte(aiResponse.getSecteurDetecte());
                cv.setPaysDetecte(aiResponse.getPaysDetecte());
                
                // Conversion List -> String pour l'entité
                cv.setCompetencesDetectees(aiResponse.getCompetencesDetectees() != null ? String.join(", ", aiResponse.getCompetencesDetectees()) : null);
                cv.setExperiencesDetectees(aiResponse.getExperiencesDetectees() != null ? String.join("\n", aiResponse.getExperiencesDetectees()) : null);
                cv.setFormationsDetectees(aiResponse.getFormationsDetectees() != null ? String.join("\n", aiResponse.getFormationsDetectees()) : null);
                cv.setCertificationsDetectees(aiResponse.getCertificationsDetectees() != null ? String.join("\n", aiResponse.getCertificationsDetectees()) : null);
                
                log.info("Source: IA (DeepSeek)");
            } else {
                throw new BusinessException("L'API IA a retourné une réponse vide");
            }
        } catch (Exception e) {
            log.error("Erreur API IA : {}", e.getMessage());
            log.info("Source: Local (regex)");
            
            // Fallback local
            AiCvExtractionResponse fallbackResponse = fallbackLocalAnalysis(extractedText);
            cv.setPosteDetecte(fallbackResponse.getPosteDetecte());
            cv.setEntrepriseDetectee(fallbackResponse.getEntrepriseDetectee());
            cv.setSecteurDetecte(fallbackResponse.getSecteurDetecte());
            cv.setPaysDetecte(fallbackResponse.getPaysDetecte());
            
            cv.setCompetencesDetectees(fallbackResponse.getCompetencesDetectees() != null ? String.join(", ", fallbackResponse.getCompetencesDetectees()) : null);
            cv.setExperiencesDetectees(fallbackResponse.getExperiencesDetectees() != null ? String.join("\n", fallbackResponse.getExperiencesDetectees()) : null);
            cv.setFormationsDetectees(fallbackResponse.getFormationsDetectees() != null ? String.join("\n", fallbackResponse.getFormationsDetectees()) : null);
            cv.setCertificationsDetectees(fallbackResponse.getCertificationsDetectees() != null ? String.join("\n", fallbackResponse.getCertificationsDetectees()) : null);
        }

        cv = cvRepository.save(cv);
        return mapToResponse(cv);
    }

    @Override
    @Transactional
    public GestionnaireCVResponse mapperProfil(Long cvId, String email, java.util.Map<String, String> requestBody) {
        log.info("Début mapping CV vers profil — cvId={}, email={}", cvId, email);

        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        GestionnaireCV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new ResourceNotFoundException("CV non trouvé"));

        if (!cv.getUtilisateur().getId().equals(user.getId())) {
            throw new BusinessException("Vous n'êtes pas autorisé à mapper ce CV");
        }

        ProfilUtilisateur profil = profilUtilisateurRepository.findByUtilisateurId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profil non trouvé"));

        String competences = resolveValue(requestBody, "competences", cv.getCompetencesDetectees());
        String experiences = resolveValue(requestBody, "experiences", cv.getExperiencesDetectees());

        log.info("Compétences reçues : {}", competences);

        if (competences != null && !competences.isBlank()) {
            profil.setCompetences(competences);
        }
        if (experiences != null && !experiences.isBlank()) {
            profil.setExperiences(experiences);
        }

        profilUtilisateurRepository.save(profil);
        log.info("Profil sauvegardé — profilId={}", profil.getId());

        if (user instanceof Alumni alumni) {
            String posteActuel = resolveValue(requestBody, "posteActuel", cv.getPosteDetecte());
            String entreprise  = resolveValue(requestBody, "entreprise",  cv.getEntrepriseDetectee());
            String secteur     = resolveValue(requestBody, "secteur",     cv.getSecteurDetecte());
            String pays        = resolveValue(requestBody, "pays",        cv.getPaysDetecte());

            log.info("posteActuel reçu : {}", posteActuel);
            log.info("entreprise reçue : {}", entreprise);

            if (posteActuel != null && !posteActuel.isBlank()) alumni.setPosteActuel(posteActuel);
            if (entreprise  != null && !entreprise.isBlank())  alumni.setEntreprise(entreprise);
            if (secteur     != null && !secteur.isBlank())     alumni.setSecteur(secteur);
            if (pays        != null && !pays.isBlank())        alumni.setPays(pays);

            utilisateurRepository.save(alumni);
            log.info("Alumni sauvegardé — alumniId={}", alumni.getId());
        }

        return mapToResponse(cv);
    }

    private String resolveValue(java.util.Map<String, String> body, String key, String fallback) {
        if (body != null) {
            String val = body.get(key);
            if (val != null && !val.isBlank()) return val;
        }
        return fallback;
    }

    @Override
    @Transactional
    public void deleteCV(Long cvId, String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        GestionnaireCV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new ResourceNotFoundException("CV non trouvé"));

        if (!cv.getUtilisateur().getId().equals(user.getId())) {
            throw new BusinessException("Vous n'êtes pas autorisé à supprimer ce CV");
        }

        // Supprimer le fichier physique
        if (cv.getCheminStockage() != null) {
            try {
                Files.deleteIfExists(Paths.get(cv.getCheminStockage()));
            } catch (IOException e) {
                log.warn("Impossible de supprimer le fichier du CV: {}", e.getMessage());
            }
        }

        cvRepository.delete(cv);
    }

    private AiCvExtractionResponse fallbackLocalAnalysis(String text) {
        if (text == null || text.isBlank()) {
            return AiCvExtractionResponse.builder().build();
        }

        String lowerText = text.toLowerCase();
        
        // Competences extraction
        List<String> keywordsComp = List.of("java", "javascript", "php", "python", "sql", "react", "node", "angular", "spring", "c++", "c#", "html", "css", "docker", "kubernetes", "aws", "git");
        List<String> competences = keywordsComp.stream()
                .filter(lowerText::contains)
                .map(k -> k.equals("react") ? "React.js" : k.equals("node") ? "Node.js" : k.substring(0, 1).toUpperCase() + k.substring(1))
                .toList();

        // Formations extraction
        List<String> keywordsForm = List.of("ingénieur", "licence", "master", "baccalauréat", "doctorat", "preparatoire");
        List<String> formations = new ArrayList<>();
        for (String line : text.split("\n")) {
            for (String kw : keywordsForm) {
                if (line.toLowerCase().contains(kw)) {
                    formations.add(line.trim());
                    break;
                }
            }
        }

        // Experiences extraction
        List<String> experiences = new ArrayList<>();
        for (String line : text.split("\n")) {
            if (line.contains("—") || line.contains("-") || line.toLowerCase().contains("stage") || line.toLowerCase().contains("pfe") || line.toLowerCase().contains("pfa")) {
                if (line.length() > 5 && line.length() < 100) {
                    experiences.add(line.trim());
                }
            }
        }

        // Certifications
        List<String> keywordsCert = List.of("scrum", "aws", "oracle", "ccna", "istqb", "pmp");
        List<String> certifications = keywordsCert.stream()
                .filter(lowerText::contains)
                .map(String::toUpperCase)
                .toList();

        // Simple mock detection for other fields
        String poste = null;
        if (lowerText.contains("élève ingénieur")) {
            poste = "Élève Ingénieur en Informatique";
        } else if (lowerText.contains("développeur")) {
            poste = "Développeur";
        }

        String entreprise = null;
        if (lowerText.contains("systeo digital")) {
            entreprise = "Systeo Digital";
        } else if (lowerText.contains("clinisys")) {
            entreprise = "Clinisys";
        }

        String secteur = null;
        if (lowerText.contains("informatique") || lowerText.contains("software")) {
            secteur = "Informatique";
        }

        String pays = null;
        if (lowerText.contains("tunisie")) {
            pays = "Tunisie";
        } else if (lowerText.contains("france")) {
            pays = "France";
        }

        return AiCvExtractionResponse.builder()
                .posteDetecte(poste)
                .entrepriseDetectee(entreprise)
                .secteurDetecte(secteur)
                .paysDetecte(pays)
                .competencesDetectees(competences)
                .experiencesDetectees(experiences)
                .formationsDetectees(formations)
                .certificationsDetectees(certifications)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GestionnaireCVResponse getMyCV(String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        GestionnaireCV cv = cvRepository.findByUtilisateurId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Aucun CV trouvé pour cet utilisateur"));

        return mapToResponse(cv);
    }

    private GestionnaireCVResponse mapToResponse(GestionnaireCV cv) {
        return GestionnaireCVResponse.builder()
                .id(cv.getId())
                .nomFichier(cv.getNomFichier())
                .cheminStockage(cv.getCheminStockage())
                .dateDepot(cv.getDateDepot())
                .contenuExtrait(cv.getContenuBrut() != null && !cv.getContenuBrut().isBlank())
                .posteDetecte(cv.getPosteDetecte())
                .entrepriseDetectee(cv.getEntrepriseDetectee())
                .secteurDetecte(cv.getSecteurDetecte())
                .paysDetecte(cv.getPaysDetecte())
                .competencesDetectees(stringToList(cv.getCompetencesDetectees(), ", "))
                .experiencesDetectees(stringToList(cv.getExperiencesDetectees(), "\n"))
                .formationsDetectees(stringToList(cv.getFormationsDetectees(), "\n"))
                .certificationsDetectees(stringToList(cv.getCertificationsDetectees(), "\n"))
                .build();
    }

    private List<String> stringToList(String value, String delimiter) {
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }
        return List.of(value.split(delimiter));
    }
}
