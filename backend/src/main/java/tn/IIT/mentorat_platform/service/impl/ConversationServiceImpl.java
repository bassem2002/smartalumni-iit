package tn.IIT.mentorat_platform.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.IIT.mentorat_platform.dto.request.MessageRequest;
import tn.IIT.mentorat_platform.dto.response.ConversationResponse;
import tn.IIT.mentorat_platform.dto.response.MessageResponse;
import tn.IIT.mentorat_platform.entity.Conversation;
import tn.IIT.mentorat_platform.entity.Message;
import tn.IIT.mentorat_platform.entity.Utilisateur;
import tn.IIT.mentorat_platform.exception.ResourceNotFoundException;
import tn.IIT.mentorat_platform.exception.UnauthorizedException;
import tn.IIT.mentorat_platform.mapper.MessageMapper;
import tn.IIT.mentorat_platform.repository.ConversationRepository;
import tn.IIT.mentorat_platform.repository.MessageRepository;
import tn.IIT.mentorat_platform.repository.UtilisateurRepository;
import tn.IIT.mentorat_platform.service.ConversationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MessageMapper messageMapper;
    private final tn.IIT.mentorat_platform.service.storage.StorageService storageService;

    @Override
    @Transactional
    public MessageResponse envoyerMessageAvecFichier(String email, Long conversationId, String contenu, org.springframework.web.multipart.MultipartFile file, String type) {
        Utilisateur user = getUtilisateur(email);
        Conversation conversation = verifyParticipant(user, conversationId);

        String fileUrl = storageService.save(file);

        Message message = Message.builder()
                .contenu(contenu != null ? contenu : "")
                .conversation(conversation)
                .expediteur(user)
                .fileUrl(fileUrl)
                .fileType(type)
                .build();

        message = messageRepository.save(message);
        return messageMapper.toResponse(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getMesConversations(String email) {
        Utilisateur user = getUtilisateur(email);
        List<Conversation> conversations = conversationRepository.findAllByUserId(user.getId());

        return conversations.stream().map(c -> {
            long nonLus = messageRepository.countByConversationIdAndLuFalse(c.getId());
            // Pour être précis on devrait filtrer les non-lus destinés à l'utilisateur courant
            // countByConversationIdAndLuFalseAndExpediteurIdNot
            
            MessageResponse dernierMsg = c.getMessages().isEmpty() ? null :
                    messageMapper.toResponse(c.getMessages().get(c.getMessages().size() - 1));

            return ConversationResponse.builder()
                    .id(c.getId())
                    .dateCreation(c.getDateCreation())
                    .demandeId(c.getDemande().getId())
                    .etudiantId(c.getDemande().getEtudiant().getId())
                    .etudiantNom(c.getDemande().getEtudiant().getNom())
                    .etudiantPrenom(c.getDemande().getEtudiant().getPrenom())
                    .alumniId(c.getDemande().getAlumni().getId())
                    .alumniNom(c.getDemande().getAlumni().getNom())
                    .alumniPrenom(c.getDemande().getAlumni().getPrenom())
                    .nombreMessages(c.getMessages().size())
                    .messagesNonLus(nonLus)
                    .dernierMessage(dernierMsg)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> getMessages(String email, Long conversationId, int page, int size) {
        Utilisateur user = getUtilisateur(email);
        verifyParticipant(user, conversationId);

        return messageRepository.findByConversationIdOrderByDateEnvoiAsc(
                conversationId, PageRequest.of(page, size))
                .map(messageMapper::toResponse);
    }

    @Override
    @Transactional
    public MessageResponse envoyerMessage(String email, Long conversationId, MessageRequest request) {
        Utilisateur user = getUtilisateur(email);
        Conversation conversation = verifyParticipant(user, conversationId);

        Message message = Message.builder()
                .contenu(request.getContenu())
                .conversation(conversation)
                .expediteur(user)
                .build();

        message = messageRepository.save(message);
        return messageMapper.toResponse(message);
    }

    @Override
    @Transactional
    public void marquerMessagesCommeLus(String email, Long conversationId) {
        Utilisateur user = getUtilisateur(email);
        verifyParticipant(user, conversationId);

        List<Message> nonLus = messageRepository
                .findByConversationIdAndLuFalseAndExpediteurIdNot(conversationId, user.getId());

        nonLus.forEach(m -> m.setLu(true));
        messageRepository.saveAll(nonLus);
    }

    // --- Helpers ---

    private Utilisateur getUtilisateur(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }

    private Conversation verifyParticipant(Utilisateur user, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

        Long etudiantId = conversation.getDemande().getEtudiant().getId();
        Long alumniId = conversation.getDemande().getAlumni().getId();

        if (!user.getId().equals(etudiantId) && !user.getId().equals(alumniId)) {
            throw new UnauthorizedException("Vous n'êtes pas participant à cette conversation");
        }
        return conversation;
    }
}
