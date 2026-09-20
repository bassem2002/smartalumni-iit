package tn.IIT.mentorat_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.IIT.mentorat_platform.dto.request.MessageRequest;
import tn.IIT.mentorat_platform.dto.response.ConversationResponse;
import tn.IIT.mentorat_platform.dto.response.MessageResponse;
import tn.IIT.mentorat_platform.service.ConversationService;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Messagerie entre étudiant et mentor")
@SecurityRequirement(name = "BearerAuth")
public class ConversationController {

    private final ConversationService conversationService;

    @Operation(summary = "Lister les conversations", description = "Retourne toutes les conversations de l'utilisateur.")
    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getMesConversations(Authentication authentication) {
        return ResponseEntity.ok(conversationService.getMesConversations(authentication.getName()));
    }

    @Operation(summary = "Lire les messages", description = "Historique paginé des messages.")
    @GetMapping("/{id}/messages")
    public ResponseEntity<Page<MessageResponse>> getMessages(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(conversationService.getMessages(authentication.getName(), id, page, size));
    }

    @Operation(summary = "Envoyer un message", description = "Envoie un message dans une conversation existante.")
    @PostMapping("/{id}/messages")
    public ResponseEntity<MessageResponse> envoyerMessage(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody MessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(conversationService.envoyerMessage(authentication.getName(), id, request));
    }

    @Operation(summary = "Envoyer un fichier", description = "Envoie un fichier ou un vocal dans une conversation.")
    @PostMapping("/{id}/messages/files")
    public ResponseEntity<MessageResponse> envoyerMessageAvecFichier(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam(value = "contenu", required = false) String contenu,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam("type") String type) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(conversationService.envoyerMessageAvecFichier(authentication.getName(), id, contenu, file, type));
    }

    @Operation(summary = "Marquer comme lu", description = "Marque les messages entrants de la conversation comme lus.")
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> marquerCommeLus(
            Authentication authentication,
            @PathVariable Long id) {
        conversationService.marquerMessagesCommeLus(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
