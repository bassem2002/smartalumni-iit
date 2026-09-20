package tn.IIT.mentorat_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.IIT.mentorat_platform.dto.response.RecommandationIAResponse;
import tn.IIT.mentorat_platform.service.RecommandationService;

import java.util.List;

@RestController
@RequestMapping("/api/recommandations")
@RequiredArgsConstructor
@Tag(name = "Recommandation", description = "Moteur de matching Etudiant/Mentor basé sur un scoring métier")
@SecurityRequirement(name = "BearerAuth")
public class RecommandationController {

    private final RecommandationService recommandationService;

    @Operation(summary = "Générer de nouvelles recommandations", description = "Calcule un score métier pour classer les mentors compatibles et garde les 4 meilleurs.")
    @PostMapping("/generer")
    @PreAuthorize("hasAuthority('ROLE_ETUDIANT')")
    public ResponseEntity<List<RecommandationIAResponse>> genererRecommandations(Authentication authentication) {
        return ResponseEntity.ok(recommandationService.genererRecommandations(authentication.getName()));
    }

    @Operation(summary = "Voir mes recommandations", description = "Retourne les mentors recommandés calculés pour l'étudiant connecté.")
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_ETUDIANT')")
    public ResponseEntity<List<RecommandationIAResponse>> getMesRecommandations(Authentication authentication) {
        return ResponseEntity.ok(recommandationService.getMesRecommandations(authentication.getName()));
    }
}
