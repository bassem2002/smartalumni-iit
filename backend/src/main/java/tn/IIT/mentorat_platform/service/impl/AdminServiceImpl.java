package tn.IIT.mentorat_platform.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.IIT.mentorat_platform.dto.response.StatistiquesResponse;
import tn.IIT.mentorat_platform.dto.response.TitreProfessionnelResponse;
import tn.IIT.mentorat_platform.dto.response.UtilisateurResponse;
import tn.IIT.mentorat_platform.entity.Etudiant;
import tn.IIT.mentorat_platform.entity.TitreProfessionnel;
import tn.IIT.mentorat_platform.entity.Utilisateur;
import tn.IIT.mentorat_platform.enums.StatutDemande;
import tn.IIT.mentorat_platform.enums.StatutVerification;
import tn.IIT.mentorat_platform.exception.ResourceNotFoundException;
import tn.IIT.mentorat_platform.mapper.TitreMapper;
import tn.IIT.mentorat_platform.repository.*;
import tn.IIT.mentorat_platform.service.AdminService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UtilisateurRepository utilisateurRepository;
    private final EtudiantRepository etudiantRepository;
    private final AlumniRepository alumniRepository;
    private final DemandeMentoratRepository demandeMentoratRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final TitreProfessionnelRepository titreProfessionnelRepository;
    private final GestionnaireCVRepository cvRepository;
    private final TitreMapper titreMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponse> listerUtilisateurs() {
        return utilisateurRepository.findAll().stream()
                .map(this::mapToUtilisateurResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void supprimerUtilisateur(Long utilisateurId) {
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new ResourceNotFoundException("Utilisateur", "id", utilisateurId);
        }
        utilisateurRepository.deleteById(utilisateurId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TitreProfessionnelResponse> listerTitresEnAttente() {
        return titreProfessionnelRepository.findByStatutVerification(StatutVerification.EN_ATTENTE)
                .stream()
                .map(titreMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TitreProfessionnelResponse validerTitre(Long titreId, StatutVerification statut) {
        TitreProfessionnel titre = titreProfessionnelRepository.findById(titreId)
                .orElseThrow(() -> new ResourceNotFoundException("Titre", "id", titreId));
        
        titre.setStatutVerification(statut);
        titre = titreProfessionnelRepository.save(titre);
        
        return titreMapper.toResponse(titre);
    }

    @Override
    @Transactional(readOnly = true)
    public StatistiquesResponse getDashboardStats() {
        return StatistiquesResponse.builder()
                .totalUtilisateurs(utilisateurRepository.count())
                .totalEtudiants(etudiantRepository.count())
                .totalAlumni(alumniRepository.count())
                .totalDemandesEnAttente(demandeMentoratRepository.countByStatut(StatutDemande.EN_ATTENTE))
                .totalDemandesAcceptees(demandeMentoratRepository.countByStatut(StatutDemande.ACCEPTEE))
                .totalDemandesRefusees(demandeMentoratRepository.countByStatut(StatutDemande.REFUSEE))
                .totalMentorsDisponibles(alumniRepository.countByDisponibleMentorat(true))
                .totalConversations(conversationRepository.count())
                .totalMessages(messageRepository.count())
                .totalTitresEnAttente(titreProfessionnelRepository.countByStatutVerification(StatutVerification.EN_ATTENTE))
                .totalCVTelecharges(cvRepository.count())
                .alumniParSecteur(alumniRepository.findAll().stream()
                        .filter(a -> a.getSecteur() != null && !a.getSecteur().isBlank())
                        .collect(Collectors.groupingBy(tn.IIT.mentorat_platform.entity.Alumni::getSecteur, Collectors.counting())))
                .build();
    }

    private UtilisateurResponse mapToUtilisateurResponse(Utilisateur user) {
        String typeProfil = "ADMIN";
        String filiere = null;
        String specialite = null;
        String posteActuel = null;
        String entreprise = null;
        String secteur = null;

        if (user instanceof Etudiant e) {
            typeProfil = "ETUDIANT";
            filiere = e.getFiliere();
            specialite = e.getSpecialite();
        } else if (user instanceof tn.IIT.mentorat_platform.entity.Alumni a) {
            typeProfil = "ALUMNI";
            filiere = a.getFiliere();
            specialite = a.getSpecialite();
            posteActuel = a.getPosteActuel();
            entreprise = a.getEntreprise();
            secteur = a.getSecteur();
        }

        return UtilisateurResponse.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .role(user.getRole().getLibelle())
                .statutCompte(user.getStatutCompte())
                .dateInscription(user.getDateInscription())
                .typeProfil(typeProfil)
                .filiere(filiere)
                .specialite(specialite)
                .posteActuel(posteActuel)
                .entreprise(entreprise)
                .secteur(secteur)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<tn.IIT.mentorat_platform.dto.response.DemandesMentorResponse> listerDemandesMentor() {
        return alumniRepository.findByDemandeStatut(tn.IIT.mentorat_platform.enums.StatutDemandeMentorat.EN_ATTENTE)
                .stream()
                .map(a -> tn.IIT.mentorat_platform.dto.response.DemandesMentorResponse.builder()
                        .id(a.getId())
                        .nom(a.getNom())
                        .prenom(a.getPrenom())
                        .email(a.getEmail())
                        .secteur(a.getSecteur())
                        .posteActuel(a.getPosteActuel())
                        .entreprise(a.getEntreprise())
                        .pays(a.getPays())
                        .demandeStatut(a.getDemandeStatut())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void traiterDemandeMentor(Long alumniId, boolean approuver) {
        tn.IIT.mentorat_platform.entity.Alumni alumni = alumniRepository.findById(alumniId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni", "id", alumniId));

        if (approuver) {
            alumni.setDisponibleMentorat(true);
            alumni.setDemandeStatut(tn.IIT.mentorat_platform.enums.StatutDemandeMentorat.APPROUVEE);
        } else {
            alumni.setDemandeStatut(tn.IIT.mentorat_platform.enums.StatutDemandeMentorat.REFUSEE);
        }
        alumniRepository.save(alumni);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponse> listerMentors() {
        return alumniRepository.findByDisponibleMentorat(true).stream()
                .map(this::mapToUtilisateurResponse)
                .collect(Collectors.toList());
    }
}
