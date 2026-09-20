package tn.IIT.mentorat_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.IIT.mentorat_platform.dto.request.AlumniFilterRequest;
import tn.IIT.mentorat_platform.dto.response.AlumniSummaryResponse;
import tn.IIT.mentorat_platform.dto.response.ProfilResponse;
import tn.IIT.mentorat_platform.service.AlumniService;

@RestController
@RequestMapping("/api/alumni")
@RequiredArgsConstructor
@Tag(name = "Alumni", description = "Recherche et consultation des profils Alumni (Mentors)")
@SecurityRequirement(name = "BearerAuth")
public class AlumniController {

    private final AlumniService alumniService;

    @Operation(summary = "Rechercher des mentors", description = "Recherche paginée d'Alumni avec filtres dynamiques (secteur, poste, pays, dispo, mot-clé).")
    @PostMapping("/search")
    public ResponseEntity<Page<AlumniSummaryResponse>> searchAlumni(@RequestBody(required = false) AlumniFilterRequest request) {
        return ResponseEntity.ok(alumniService.searchAlumni(request));
    }

    @Operation(summary = "Détails d'un Alumni", description = "Récupère le profil complet d'un Alumni (expériences, compétences, titres).")
    @GetMapping("/{id}")
    public ResponseEntity<ProfilResponse> getAlumniDetails(@PathVariable Long id) {
        return ResponseEntity.ok(alumniService.getAlumniDetails(id));
    }

    @Operation(summary = "Activer/Désactiver disponibilité mentorat", description = "Modifie le statut de disponibilité d'un Alumni (Réservé aux Alumni).")
    @PatchMapping("/{id}/mentorat")
    @PreAuthorize("hasAuthority('ROLE_ALUMNI') and #id == authentication.principal.id") // ou gestion plus poussée dans le service
    public ResponseEntity<Void> toggleDisponibiliteMentorat(@PathVariable Long id) {
        alumniService.toggleDisponibiliteMentorat(id);
        return ResponseEntity.noContent().build();
    }
}
