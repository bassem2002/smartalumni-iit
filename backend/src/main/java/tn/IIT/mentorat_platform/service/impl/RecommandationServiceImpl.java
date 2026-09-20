package tn.IIT.mentorat_platform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.IIT.mentorat_platform.dto.response.RecommandationIAResponse;
import tn.IIT.mentorat_platform.entity.Alumni;
import tn.IIT.mentorat_platform.entity.Etudiant;
import tn.IIT.mentorat_platform.entity.RecommandationIA;
import tn.IIT.mentorat_platform.entity.Utilisateur;
import tn.IIT.mentorat_platform.exception.ResourceNotFoundException;
import tn.IIT.mentorat_platform.exception.UnauthorizedException;
import tn.IIT.mentorat_platform.repository.AlumniRepository;
import tn.IIT.mentorat_platform.repository.RecommandationIARepository;
import tn.IIT.mentorat_platform.repository.UtilisateurRepository;
import tn.IIT.mentorat_platform.service.RecommandationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommandationServiceImpl implements RecommandationService {

    private final RecommandationIARepository recommandationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AlumniRepository alumniRepository;

    @Override
    @Transactional
    public List<RecommandationIAResponse> genererRecommandations(String emailEtudiant) {
        Utilisateur user = utilisateurRepository.findByEmail(emailEtudiant)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (!(user instanceof Etudiant etudiant)) {
            throw new UnauthorizedException("Seul un étudiant peut demander des recommandations");
        }

        recommandationRepository.deleteByEtudiantId(etudiant.getId());

        List<ScoredMentor> scoredMentors = scorerMentors(etudiant);
        List<RecommandationIA> nouvellesRecommandations = scoredMentors.stream()
                .limit(4)
                .map(candidate -> RecommandationIA.builder()
                        .etudiant(etudiant)
                        .alumni(candidate.alumni())
                        .scoreMatching(candidate.score())
                        .raison(candidate.reason())
                        .build())
                .toList();

        nouvellesRecommandations = recommandationRepository.saveAll(nouvellesRecommandations);

        return nouvellesRecommandations.stream()
                .sorted(Comparator.comparing(RecommandationIA::getScoreMatching).reversed())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommandationIAResponse> getMesRecommandations(String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        return recommandationRepository.findByEtudiantIdOrderByScoreMatchingDesc(user.getId())
                .stream()
                .limit(4)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // --- Helpers ---

    private List<ScoredMentor> scorerMentors(Etudiant etudiant) {
        List<Alumni> mentors = alumniRepository.findByDisponibleMentorat(true);
        List<ScoredMentor> scored = new ArrayList<>();

        for (Alumni mentor : mentors) {
            ScoreResult result = computeScore(etudiant, mentor);
            if (result.score() <= 0.0) {
                continue;
            }
            scored.add(new ScoredMentor(mentor, result.score(), result.reason()));
        }

        scored.sort(Comparator.comparing(ScoredMentor::score).reversed()
                .thenComparing(candidate -> candidate.alumni().getNom(), String.CASE_INSENSITIVE_ORDER));
        return scored;
    }

    private ScoreResult computeScore(Etudiant etudiant, Alumni mentor) {
        String studentFiliere = normalizeText(etudiant.getFiliere());
        String studentSpecialite = normalizeText(etudiant.getSpecialite());
        String mentorFiliere = normalizeText(mentor.getFiliere());
        String mentorSpecialite = normalizeText(mentor.getSpecialite());
        String mentorSecteur = normalizeText(mentor.getSecteur());
        String mentorPoste = normalizeText(mentor.getPosteActuel());
        String mentorEntreprise = normalizeText(mentor.getEntreprise());
        String mentorCompetences = normalizeText(mentor.getProfil() != null ? mentor.getProfil().getCompetences() : null);
        String mentorExperiences = normalizeText(mentor.getProfil() != null ? mentor.getProfil().getExperiences() : null);
        String studentContext = normalizeText(joinNonEmpty(" ", etudiant.getFiliere(), etudiant.getSpecialite(), etudiant.getNiveauEtude()));

        double filiereScore = computeFiliereScore(studentFiliere, studentSpecialite, mentorFiliere, mentorSpecialite);
        double domaineScore = computeDomainScore(studentContext, mentorSecteur, mentorSpecialite, mentorCompetences, mentorExperiences);
        double experienceScore = computeExperienceScore(mentor);
        double complementScore = computeCompletenessBonus(mentorPoste, mentorEntreprise, mentorCompetences, mentorExperiences);

        double score = (filiereScore * 0.40)
                + (domaineScore * 0.30)
                + (experienceScore * 0.25)
                + (complementScore * 0.05);

        score = roundToTwoDecimals(score);

        List<String> reasons = new ArrayList<>();
        if (filiereScore >= 0.7) {
            reasons.add("filière alignée");
        }
        if (domaineScore >= 0.6) {
            reasons.add("domaine proche");
        }
        if (experienceScore >= 0.5) {
            reasons.add("expérience adaptée");
        }
        if (mentor.getEntreprise() != null || mentor.getPosteActuel() != null) {
            reasons.add("profil mentor complet");
        }

        String reason = reasons.isEmpty()
                ? "Score calculé à partir de la filière, du domaine et de l'expérience"
                : String.join(", ", reasons);

        return new ScoreResult(score, reason);
    }

    private double computeFiliereScore(String studentFiliere, String studentSpecialite, String mentorFiliere, String mentorSpecialite) {
        if (studentFiliere.isBlank() && studentSpecialite.isBlank()) {
            return 0.0;
        }

        if (!studentFiliere.isBlank() && studentFiliere.equals(mentorFiliere)) {
            return 1.0;
        }
        if (!studentSpecialite.isBlank() && studentSpecialite.equals(mentorSpecialite)) {
            return 1.0;
        }

        double best = 0.0;
        if (!studentFiliere.isBlank()) {
            best = Math.max(best, overlapScore(studentFiliere, joinNonEmpty(" ", mentorFiliere, mentorSpecialite)));
        }
        if (!studentSpecialite.isBlank()) {
            best = Math.max(best, overlapScore(studentSpecialite, joinNonEmpty(" ", mentorSpecialite, mentorFiliere)));
        }
        return best;
    }

    private double computeDomainScore(String studentContext, String mentorSecteur, String mentorSpecialite, String mentorCompetences, String mentorExperiences) {
        String mentorContext = joinNonEmpty(" ", mentorSecteur, mentorSpecialite, mentorCompetences, mentorExperiences);
        if (studentContext.isBlank() || mentorContext.isBlank()) {
            return 0.0;
        }
        return overlapScore(studentContext, mentorContext);
    }

    private double computeExperienceScore(Alumni mentor) {
        Integer promo = mentor.getAnneePromotion();
        if (promo != null && promo > 1900) {
            int years = Math.max(0, LocalDate.now().getYear() - promo);
            if (years >= 12) {
                return 1.0;
            }
            if (years >= 8) {
                return 0.9;
            }
            if (years >= 5) {
                return 0.75;
            }
            if (years >= 3) {
                return 0.55;
            }
            if (years >= 1) {
                return 0.35;
            }
            return 0.2;
        }

        if (mentor.getPosteActuel() != null || mentor.getEntreprise() != null) {
            return 0.6;
        }
        return 0.2;
    }

    private double computeCompletenessBonus(String mentorPoste, String mentorEntreprise, String mentorCompetences, String mentorExperiences) {
        int filled = 0;
        if (!mentorPoste.isBlank()) filled++;
        if (!mentorEntreprise.isBlank()) filled++;
        if (!mentorCompetences.isBlank()) filled++;
        if (!mentorExperiences.isBlank()) filled++;
        return filled / 4.0;
    }

    private double overlapScore(String left, String right) {
        Set<String> leftTokens = tokenize(left);
        Set<String> rightTokens = tokenize(right);
        if (leftTokens.isEmpty() || rightTokens.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new LinkedHashSet<>(leftTokens);
        intersection.retainAll(rightTokens);
        if (!intersection.isEmpty()) {
            return Math.min(1.0, 0.6 + (intersection.size() * 0.15));
        }

        for (String leftToken : leftTokens) {
            for (String rightToken : rightTokens) {
                if (leftToken.contains(rightToken) || rightToken.contains(leftToken)) {
                    return 0.7;
                }
            }
        }
        return 0.0;
    }

    private Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(text.split("[^\\p{L}\\p{N}]+"))
                .map(String::trim)
                .map(token -> token.toLowerCase(Locale.ROOT))
                .filter(token -> !token.isBlank())
                .filter(token -> token.length() > 2)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String normalizeText(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.ROOT)
                .replace("é", "e")
                .replace("è", "e")
                .replace("ê", "e")
                .replace("à", "a")
                .replace("ù", "u")
                .replace("ô", "o")
                .replace("ï", "i")
                .replace("î", "i")
                .replace("ç", "c")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String joinNonEmpty(String delimiter, String... values) {
        return List.of(values).stream()
                .filter(v -> v != null && !v.isBlank())
                .collect(Collectors.joining(delimiter));
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private record ScoreResult(double score, String reason) {}

    private record ScoredMentor(Alumni alumni, double score, String reason) {}

    private RecommandationIAResponse mapToResponse(RecommandationIA rec) {
        return RecommandationIAResponse.builder()
                .id(rec.getId())
                .scoreMatching(rec.getScoreMatching())
                .raison(rec.getRaison())
                .dateGeneration(rec.getDateGeneration())
                .alumniId(rec.getAlumni().getId())
                .alumniNom(rec.getAlumni().getNom())
                .alumniPrenom(rec.getAlumni().getPrenom())
                .alumniPhoto(rec.getAlumni().getProfil() != null ? rec.getAlumni().getProfil().getPhoto() : null)
                .secteur(rec.getAlumni().getSecteur())
                .posteActuel(rec.getAlumni().getPosteActuel())
                .entreprise(rec.getAlumni().getEntreprise())
                .pays(rec.getAlumni().getPays())
                .competences(rec.getAlumni().getProfil() != null ? rec.getAlumni().getProfil().getCompetences() : null)
                .build();
    }
}
