package tn.IIT.mentorat_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.IIT.mentorat_platform.dto.request.DemandeMentoratRequest;
import tn.IIT.mentorat_platform.dto.request.StatutUpdateRequest;
import tn.IIT.mentorat_platform.dto.response.DemandeMentoratResponse;
import tn.IIT.mentorat_platform.service.MentoratService;

import java.util.List;

@RestController
@RequestMapping("/api/mentorat")
@RequiredArgsConstructor
@Tag(name = "Mentorat", description = "Gestion des demandes de mentorat (Match Etudiant -> Alumni)")
@SecurityRequirement(name = "BearerAuth")
public class MentoratController {

    private final MentoratService mentoratService;

    @Operation(summary = "Envoyer une demande", description = "L'étudiant demande l'accompagnement d'un mentor.")
    @PostMapping("/demandes")
    @PreAuthorize("hasAuthority('ROLE_ETUDIANT')")
    public ResponseEntity<DemandeMentoratResponse> envoyerDemande(
            Authentication authentication,
            @Valid @RequestBody DemandeMentoratRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentoratService.envoyerDemande(authentication.getName(), request));
    }

    @Operation(summary = "Accepter/Refuser une demande", description = "L'Alumni change le statut de la demande. Si acceptée, crée une Conversation.")
    @PatchMapping("/demandes/{id}/statut")
    @PreAuthorize("hasAuthority('ROLE_ALUMNI')")
    public ResponseEntity<DemandeMentoratResponse> changerStatut(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody StatutUpdateRequest request) {
        return ResponseEntity.ok(mentoratService.changerStatut(authentication.getName(), id, request));
    }

    @Operation(summary = "Lister mes demandes", description = "Retourne les demandes envoyées (si Étudiant) ou reçues (si Alumni).")
    @GetMapping("/demandes")
    public ResponseEntity<List<DemandeMentoratResponse>> getMesDemandes(Authentication authentication) {
        return ResponseEntity.ok(mentoratService.getMesDemandes(authentication.getName()));
    }
}
