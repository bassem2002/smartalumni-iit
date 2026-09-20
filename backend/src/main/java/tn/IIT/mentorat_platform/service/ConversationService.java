package tn.IIT.mentorat_platform.service;

import org.springframework.data.domain.Page;
import tn.IIT.mentorat_platform.dto.request.MessageRequest;
import tn.IIT.mentorat_platform.dto.response.ConversationResponse;
import tn.IIT.mentorat_platform.dto.response.MessageResponse;

import java.util.List;

public interface ConversationService {
    List<ConversationResponse> getMesConversations(String email);
    Page<MessageResponse> getMessages(String email, Long conversationId, int page, int size);
    MessageResponse envoyerMessage(String email, Long conversationId, MessageRequest request);
    MessageResponse envoyerMessageAvecFichier(String email, Long conversationId, String contenu, org.springframework.web.multipart.MultipartFile file, String type);
    void marquerMessagesCommeLus(String email, Long conversationId);
}
