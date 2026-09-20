package tn.IIT.mentorat_platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import tn.IIT.mentorat_platform.dto.response.AiCvExtractionResponse;
import tn.IIT.mentorat_platform.dto.response.GestionnaireCVResponse;
import tn.IIT.mentorat_platform.entity.GestionnaireCV;
import tn.IIT.mentorat_platform.entity.Alumni;
import tn.IIT.mentorat_platform.entity.ProfilUtilisateur;
import tn.IIT.mentorat_platform.entity.Utilisateur;
import tn.IIT.mentorat_platform.repository.GestionnaireCVRepository;
import tn.IIT.mentorat_platform.repository.ProfilUtilisateurRepository;
import tn.IIT.mentorat_platform.repository.UtilisateurRepository;
import tn.IIT.mentorat_platform.service.impl.CVServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CVServiceImplTest {

    @Mock
    private GestionnaireCVRepository cvRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private ProfilUtilisateurRepository profilUtilisateurRepository;

    @Mock
    private AiCvExtractionService aiCvExtractionService;

    @InjectMocks
    private CVServiceImpl cvService;

    private String email = "test@iit.ens.tn";
    private Utilisateur user;
    private ProfilUtilisateur profil;
    private MockMultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        user = new Alumni();
        user.setId(1L);
        user.setEmail(email);

        profil = new ProfilUtilisateur();
        profil.setId(1L);
        profil.setUtilisateur(user);

        multipartFile = new MockMultipartFile(
                "file",
                "cv.txt",
                "text/plain",
                "Java developer\nStage chez Systeo Digital en Tunisie en 2025\nDiplôme d'Ingénieur en informatique".getBytes()
        );

        ReflectionTestUtils.setField(cvService, "uploadDir", "target/uploads/cv/");
    }

    @Test
    void testUploadCV_Success() throws IOException {
        // Arrange
        when(utilisateurRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(profilUtilisateurRepository.findByUtilisateurId(user.getId())).thenReturn(Optional.of(profil));
        when(cvRepository.findByUtilisateurId(user.getId())).thenReturn(Optional.empty());
        when(cvRepository.save(any(GestionnaireCV.class))).thenAnswer(invocation -> {
            GestionnaireCV savedCv = invocation.getArgument(0);
            savedCv.setId(10L); // Simuler l'attribution d'un ID
            return savedCv;
        });

        // Act
        GestionnaireCVResponse response = cvService.uploadCV(email, multipartFile);

        // Assert
        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertNull(response.getPosteDetecte()); // Pas d'analyse auto
        verify(aiCvExtractionService, never()).extractFromText(any());
    }

    @Test
    void testAnalyserCV_AiSuccess() throws IOException {
        // Arrange
        GestionnaireCV cv = new GestionnaireCV();
        cv.setId(10L);
        cv.setUtilisateur(user);
        cv.setContenuBrut("Java developer\nStage chez Systeo Digital en Tunisie en 2025\nDiplôme d'Ingénieur en informatique");

        when(utilisateurRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(cvRepository.findById(10L)).thenReturn(Optional.of(cv));

        AiCvExtractionResponse aiResponse = AiCvExtractionResponse.builder()
                .posteDetecte("Développeur Java")
                .entrepriseDetectee("Systeo Digital")
                .secteurDetecte("Informatique")
                .paysDetecte("Tunisie")
                .competencesDetectees(List.of("Java", "Spring"))
                .experiencesDetectees(List.of("Systeo Digital - Stage - 2025"))
                .formationsDetectees(List.of("Diplôme d'Ingénieur"))
                .certificationsDetectees(new ArrayList<>())
                .build();

        when(aiCvExtractionService.extractFromText(any())).thenReturn(aiResponse);
        when(cvRepository.save(any(GestionnaireCV.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        GestionnaireCVResponse response = cvService.analyserCV(10L, email);

        // Assert
        assertNotNull(response);
        assertEquals("Développeur Java", response.getPosteDetecte());
        assertEquals("Systeo Digital", response.getEntrepriseDetectee());
        assertTrue(response.getCompetencesDetectees().contains("Java"));
        verify(aiCvExtractionService, times(1)).extractFromText(any());
    }

    @Test
    void testAnalyserCV_AiFailureFallbackSuccess() throws IOException {
        // Arrange
        GestionnaireCV cv = new GestionnaireCV();
        cv.setId(10L);
        cv.setUtilisateur(user);
        cv.setContenuBrut("Java developer\nStage chez Systeo Digital en Tunisie en 2025\nDiplôme d'Ingénieur en informatique");

        when(utilisateurRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(cvRepository.findById(10L)).thenReturn(Optional.of(cv));

        // AI throws exception -> should fallback to local keyword analysis
        when(aiCvExtractionService.extractFromText(any())).thenThrow(new RuntimeException("API key invalid"));
        when(cvRepository.save(any(GestionnaireCV.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        GestionnaireCVResponse response = cvService.analyserCV(10L, email);

        // Assert
        assertNotNull(response);
        assertTrue(response.getCompetencesDetectees().contains("Java"));
        assertEquals("Systeo Digital", response.getEntrepriseDetectee());
        assertEquals("Tunisie", response.getPaysDetecte());
        verify(aiCvExtractionService, times(1)).extractFromText(any());
    }
}
