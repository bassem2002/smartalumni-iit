package tn.IIT.mentorat_platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.IIT.mentorat_platform.dto.response.RecommandationIAResponse;
import tn.IIT.mentorat_platform.entity.Alumni;
import tn.IIT.mentorat_platform.entity.Etudiant;
import tn.IIT.mentorat_platform.entity.ProfilUtilisateur;
import tn.IIT.mentorat_platform.entity.RecommandationIA;
import tn.IIT.mentorat_platform.entity.Utilisateur;
import tn.IIT.mentorat_platform.enums.StatutCompte;
import tn.IIT.mentorat_platform.repository.AlumniRepository;
import tn.IIT.mentorat_platform.repository.RecommandationIARepository;
import tn.IIT.mentorat_platform.repository.UtilisateurRepository;
import tn.IIT.mentorat_platform.service.impl.RecommandationServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommandationServiceImplTest {

    @Mock
    private RecommandationIARepository recommandationRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private AlumniRepository alumniRepository;

    @InjectMocks
    private RecommandationServiceImpl recommendationService;

    private Etudiant etudiant;
    private Alumni mentor1;
    private Alumni mentor2;
    private Alumni mentor3;
    private Alumni mentor4;
    private Alumni mentor5;

    @BeforeEach
    void setUp() {
        etudiant = new Etudiant();
        etudiant.setId(1L);
        etudiant.setNom("Etudiant");
        etudiant.setPrenom("Test");
        etudiant.setEmail("etudiant@iit.tn");
        etudiant.setDateInscription(LocalDateTime.now());
        etudiant.setStatutCompte(StatutCompte.ACTIF);
        etudiant.setFiliere("Informatique");
        etudiant.setSpecialite("Data Science");
        etudiant.setNiveauEtude("Licence");

        mentor1 = buildMentor(10L, "Samir", "Data", "Data Science", "Data & IA", 2018, "Python, Machine Learning", "Data engineering");
        mentor2 = buildMentor(11L, "Ines", "Informatique", "Data Science", "Informatique", 2020, "Python, SQL", "Analyse de données");
        mentor3 = buildMentor(12L, "Ali", "Réseaux", "Cybersécurité", "Cybersécurité", 2019, "Security, Network", "Sécurité réseau");
        mentor4 = buildMentor(13L, "Lina", "Informatique", "Développement Web", "Web", 2022, "Java, Spring", "Développement logiciel");
        mentor5 = buildMentor(14L, "Yassine", "Gestion", "Management", "Business", 2017, "Finance", "Gestion de projet");
        mentor5.setDisponibleMentorat(false);
    }

    @Test
    void genererRecommandations_shouldReturnTop4SortedByScore() {
        when(utilisateurRepository.findByEmail(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(alumniRepository.findByDisponibleMentorat(true)).thenReturn(List.of(mentor1, mentor2, mentor3, mentor4, mentor5));
        when(recommandationRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<RecommandationIAResponse> result = recommendationService.genererRecommandations(etudiant.getEmail());

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(mentor1.getId(), result.get(0).getAlumniId());
        assertTrue(result.get(0).getScoreMatching() >= result.get(1).getScoreMatching());
        assertTrue(result.get(1).getScoreMatching() >= result.get(2).getScoreMatching());
        assertTrue(result.get(2).getScoreMatching() >= result.get(3).getScoreMatching());
    }

    @Test
    void getMesRecommandations_shouldLimitToFour() {
        when(utilisateurRepository.findByEmail(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(recommandationRepository.findByEtudiantIdOrderByScoreMatchingDesc(etudiant.getId()))
                .thenReturn(List.of(
                        buildRecommendation(mentor1, 0.98),
                        buildRecommendation(mentor2, 0.91),
                        buildRecommendation(mentor3, 0.84),
                        buildRecommendation(mentor4, 0.73),
                        buildRecommendation(mentor5, 0.61)
                ));

        List<RecommandationIAResponse> result = recommendationService.getMesRecommandations(etudiant.getEmail());

        assertEquals(4, result.size());
        assertEquals(mentor1.getId(), result.get(0).getAlumniId());
        assertEquals(mentor4.getId(), result.get(3).getAlumniId());
    }

    private Alumni buildMentor(Long id, String prenom, String filiere, String specialite, String secteur, Integer anneePromotion, String competences, String experiences) {
        Alumni mentor = new Alumni();
        mentor.setId(id);
        mentor.setNom("Mentor");
        mentor.setPrenom(prenom);
        mentor.setEmail(prenom.toLowerCase() + "@iit.tn");
        mentor.setDateInscription(LocalDateTime.now());
        mentor.setStatutCompte(StatutCompte.ACTIF);
        mentor.setFiliere(filiere);
        mentor.setSpecialite(specialite);
        mentor.setSecteur(secteur);
        mentor.setAnneePromotion(anneePromotion);
        mentor.setDisponibleMentorat(true);
        mentor.setPosteActuel("Développeur");
        mentor.setEntreprise("Entreprise");

        ProfilUtilisateur profil = new ProfilUtilisateur();
        profil.setCompetences(competences);
        profil.setExperiences(experiences);
        mentor.setProfil(profil);

        return mentor;
    }

    private RecommandationIA buildRecommendation(Alumni mentor, double score) {
        RecommandationIA rec = new RecommandationIA();
        rec.setAlumni(mentor);
        rec.setScoreMatching(score);
        rec.setRaison("test");
        return rec;
    }
}
