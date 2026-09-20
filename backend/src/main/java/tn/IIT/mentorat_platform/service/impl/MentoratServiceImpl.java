package tn.IIT.mentorat_platform.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.IIT.mentorat_platform.dto.request.DemandeMentoratRequest;
import tn.IIT.mentorat_platform.dto.request.StatutUpdateRequest;
import tn.IIT.mentorat_platform.dto.response.DemandeMentoratResponse;
import tn.IIT.mentorat_platform.entity.*;
import tn.IIT.mentorat_platform.enums.StatutDemande;
import tn.IIT.mentorat_platform.exception.BusinessException;
import tn.IIT.mentorat_platform.exception.ResourceNotFoundException;
import tn.IIT.mentorat_platform.exception.UnauthorizedException;
import tn.IIT.mentorat_platform.mapper.DemandeMentoratMapper;
import tn.IIT.mentorat_platform.repository.AlumniRepository;
import tn.IIT.mentorat_platform.repository.ConversationRepository;
import tn.IIT.mentorat_platform.repository.DemandeMentoratRepository;
import tn.IIT.mentorat_platform.repository.UtilisateurRepository;
import tn.IIT.mentorat_platform.service.MentoratService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentoratServiceImpl implements MentoratService {

    private final UtilisateurRepository utilisateurRepository;
    private final AlumniRepository alumniRepository;
    private final DemandeMentoratRepository demandeMentoratRepository;
    private final ConversationRepository conversationRepository;
    private final DemandeMentoratMapper demandeMentoratMapper;

    @Override
    @Transactional
    public DemandeMentoratResponse envoyerDemande(String email, DemandeMentoratRequest request) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (!(user instanceof Etudiant etudiant)) {
            throw new UnauthorizedException("Seul un étudiant peut envoyer une demande de mentorat");
        }

        Alumni alumni = alumniRepository.findById(request.getAlumniId())
                .orElseThrow(() -> new ResourceNotFoundException("Alumni", "id", request.getAlumniId()));

        if (!alumni.getDisponibleMentorat()) {
            throw new BusinessException("Cet Alumni n'est pas disponible pour le mentorat");
        }

        if (demandeMentoratRepository.existsByEtudiantIdAndAlumniId(etudiant.getId(), alumni.getId())) {
            throw new BusinessException("Vous avez déjà envoyé une demande à ce mentor");
        }

        DemandeMentorat demande = DemandeMentorat.builder()
                .etudiant(etudiant)
                .alumni(alumni)
                .message(request.getMessage())
                .statut(StatutDemande.EN_ATTENTE)
                .build();

        demande = demandeMentoratRepository.save(demande);

        return demandeMentoratMapper.toResponse(demande);
    }

    @Override
    @Transactional
    public DemandeMentoratResponse changerStatut(String email, Long demandeId, StatutUpdateRequest request) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        DemandeMentorat demande = demandeMentoratRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande", "id", demandeId));

        if (!(user instanceof Alumni alumni) || !demande.getAlumni().getId().equals(alumni.getId())) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier le statut de cette demande");
        }

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new BusinessException("Seules les demandes en attente peuvent être modifiées");
        }

        demande.setStatut(request.getStatut());

        // Créer une conversation si acceptée
        if (request.getStatut() == StatutDemande.ACCEPTEE) {
            Conversation conversation = Conversation.builder()
                    .demande(demande)
                    .build();
            conversationRepository.save(conversation);
            demande.setConversation(conversation);
        }

        demande = demandeMentoratRepository.save(demande);
        return demandeMentoratMapper.toResponse(demande);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandeMentoratResponse> getMesDemandes(String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        List<DemandeMentorat> demandes;
        if (user instanceof Etudiant etudiant) {
            demandes = demandeMentoratRepository.findByEtudiantId(etudiant.getId());
        } else if (user instanceof Alumni alumni) {
            demandes = demandeMentoratRepository.findByAlumniId(alumni.getId());
        } else {
            throw new UnauthorizedException("Type d'utilisateur invalide");
        }

        return demandes.stream()
                .map(demandeMentoratMapper::toResponse)
                .collect(Collectors.toList());
    }
}
