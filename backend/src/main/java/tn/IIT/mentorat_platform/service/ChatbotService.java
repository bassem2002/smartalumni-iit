package tn.IIT.mentorat_platform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.IIT.mentorat_platform.entity.Alumni;
import tn.IIT.mentorat_platform.entity.Etudiant;
import tn.IIT.mentorat_platform.entity.Utilisateur;
import tn.IIT.mentorat_platform.enums.StatutDemandeMentorat;
import tn.IIT.mentorat_platform.repository.AlumniRepository;
import tn.IIT.mentorat_platform.repository.EtudiantRepository;
import tn.IIT.mentorat_platform.repository.UtilisateurRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatbotService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final AlumniRepository alumniRepository;
    private final EtudiantRepository etudiantRepository;
    private final UtilisateurRepository utilisateurRepository;

    public ChatbotService(AlumniRepository alumniRepository,
                          EtudiantRepository etudiantRepository,
                          UtilisateurRepository utilisateurRepository) {
        this.restTemplate = new RestTemplate();
        this.alumniRepository = alumniRepository;
        this.etudiantRepository = etudiantRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Construit un contexte riche à partir des données réelles de la base de données.
     * Ce contexte est injecté dans le prompt Gemini avant la question de l'admin.
     */
    private String construireContexteBD() {
        StringBuilder context = new StringBuilder();
        context.append("=== DONNÉES RÉELLES DE LA PLATEFORME SMARTALUMNI IIT ===\n\n");

        // --- Statistiques générales ---
        List<Utilisateur> tousUtilisateurs = utilisateurRepository.findAll();
        long totalUtilisateurs = tousUtilisateurs.size();

        List<Alumni> tousAlumni = alumniRepository.findAll();
        long totalAlumni = tousAlumni.size();

        List<Etudiant> tousEtudiants = etudiantRepository.findAll();
        long totalEtudiants = tousEtudiants.size();

        long mentorsActifs = alumniRepository.countByDisponibleMentorat(true);
        List<Alumni> alumniApprouves = alumniRepository.findByDemandeStatut(StatutDemandeMentorat.APPROUVEE);

        context.append("--- STATISTIQUES GÉNÉRALES ---\n");
        context.append("Total utilisateurs inscrits : ").append(totalUtilisateurs).append("\n");
        context.append("Total Alumni : ").append(totalAlumni).append("\n");
        context.append("Total Etudiants : ").append(totalEtudiants).append("\n");
        context.append("Mentors actifs (disponibles pour le mentorat) : ").append(mentorsActifs).append("\n");
        context.append("Alumni avec demande de mentorat approuvée : ").append(alumniApprouves.size()).append("\n\n");

        // --- Liste des mentors actifs ---
        context.append("--- LISTE DES MENTORS ACTIFS ---\n");
        List<Alumni> mentors = alumniRepository.findByDemandeStatut(StatutDemandeMentorat.APPROUVEE)
                .stream()
                .filter(a -> Boolean.TRUE.equals(a.getDisponibleMentorat()))
                .collect(Collectors.toList());

        if (mentors.isEmpty()) {
            // Si pas d'APPROUVEE, prenons tous les disponibles
            mentors = alumniRepository.findAll()
                    .stream()
                    .filter(a -> Boolean.TRUE.equals(a.getDisponibleMentorat()))
                    .collect(Collectors.toList());
        }

        if (mentors.isEmpty()) {
            context.append("Aucun mentor actif pour le moment.\n");
        } else {
            for (Alumni mentor : mentors) {
                context.append("- Nom : ").append(mentor.getPrenom()).append(" ").append(mentor.getNom());
                if (mentor.getPosteActuel() != null) context.append(" | Poste : ").append(mentor.getPosteActuel());
                if (mentor.getEntreprise() != null) context.append(" | Entreprise : ").append(mentor.getEntreprise());
                if (mentor.getSecteur() != null) context.append(" | Secteur : ").append(mentor.getSecteur());
                if (mentor.getPays() != null) context.append(" | Pays : ").append(mentor.getPays());
                context.append("\n");
            }
        }

        // --- Liste de tous les alumni ---
        context.append("\n--- LISTE DE TOUS LES ALUMNI ---\n");
        if (tousAlumni.isEmpty()) {
            context.append("Aucun alumni inscrit.\n");
        } else {
            for (Alumni alumni : tousAlumni) {
                context.append("- ").append(alumni.getPrenom()).append(" ").append(alumni.getNom());
                if (alumni.getSecteur() != null) context.append(" | Secteur : ").append(alumni.getSecteur());
                if (alumni.getPosteActuel() != null) context.append(" | Poste : ").append(alumni.getPosteActuel());
                if (alumni.getEntreprise() != null) context.append(" | Entreprise : ").append(alumni.getEntreprise());
                if (alumni.getPays() != null) context.append(" | Pays : ").append(alumni.getPays());
                context.append(" | Mentor disponible : ").append(Boolean.TRUE.equals(alumni.getDisponibleMentorat()) ? "Oui" : "Non");
                context.append("\n");
            }
        }

        // --- Statistiques par spécialité (Etudiants) ---
        context.append("\n--- STATISTIQUES ETUDIANTS PAR SPÉCIALITÉ ---\n");
        Map<String, Long> parSpecialite = tousEtudiants.stream()
                .filter(e -> e.getSpecialite() != null && !e.getSpecialite().isBlank())
                .collect(Collectors.groupingBy(Etudiant::getSpecialite, Collectors.counting()));

        if (parSpecialite.isEmpty()) {
            context.append("Aucune donnée de spécialité disponible.\n");
        } else {
            parSpecialite.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .forEach(e -> context.append("- ").append(e.getKey()).append(" : ").append(e.getValue()).append(" étudiant(s)\n"));
        }

        // --- Statistiques par filière ---
        context.append("\n--- STATISTIQUES ETUDIANTS PAR FILIÈRE ---\n");
        Map<String, Long> parFiliere = tousEtudiants.stream()
                .filter(e -> e.getFiliere() != null && !e.getFiliere().isBlank())
                .collect(Collectors.groupingBy(Etudiant::getFiliere, Collectors.counting()));

        if (parFiliere.isEmpty()) {
            context.append("Aucune donnée de filière disponible.\n");
        } else {
            parFiliere.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .forEach(e -> context.append("- ").append(e.getKey()).append(" : ").append(e.getValue()).append(" étudiant(s)\n"));
        }

        // --- Statistiques alumni par secteur ---
        context.append("\n--- ALUMNI PAR SECTEUR PROFESSIONNEL ---\n");
        Map<String, Long> parSecteur = tousAlumni.stream()
                .filter(a -> a.getSecteur() != null && !a.getSecteur().isBlank())
                .collect(Collectors.groupingBy(Alumni::getSecteur, Collectors.counting()));

        if (parSecteur.isEmpty()) {
            context.append("Aucune donnée de secteur disponible.\n");
        } else {
            parSecteur.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .forEach(e -> context.append("- ").append(e.getKey()).append(" : ").append(e.getValue()).append(" alumni\n"));
        }

        // --- Alumni par pays ---
        context.append("\n--- ALUMNI PAR PAYS ---\n");
        Map<String, Long> parPays = tousAlumni.stream()
                .filter(a -> a.getPays() != null && !a.getPays().isBlank())
                .collect(Collectors.groupingBy(Alumni::getPays, Collectors.counting()));

        if (parPays.isEmpty()) {
            context.append("Aucune donnée de pays disponible.\n");
        } else {
            parPays.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .forEach(e -> context.append("- ").append(e.getKey()).append(" : ").append(e.getValue()).append(" alumni\n"));
        }

        context.append("\n=== FIN DES DONNÉES ===\n");
        return context.toString();
    }

    public String interrogerIA(String questionAdmin) {
        String url = apiUrl + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construire le contexte à partir de la BDD
        String contexteBD = construireContexteBD();

        // Prompt complet : instructions système + données réelles + question
        String promptComplet = "Tu es un assistant expert en analyse de données pour l'université IIT. " +
                "Tu dois répondre UNIQUEMENT en te basant sur les données ci-dessous qui proviennent de la base de données réelle de la plateforme SmartAlumni IIT. " +
                "Ne jamais inventer ou supposer des données. Si l'information n'est pas dans les données, dis-le clairement. " +
                "Réponds toujours en français, de manière concise et professionnelle.\n\n" +
                contexteBD +
                "\nQuestion de l'administrateur : " + questionAdmin;

        Map<String, Object> part = new HashMap<>();
        part.put("text", promptComplet);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(part));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(content));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> contentMap = (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");
                    if (!parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }
            return "Désolé, je n'ai pas pu générer une réponse.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la communication avec l'IA: " + e.getMessage();
        }
    }
}
