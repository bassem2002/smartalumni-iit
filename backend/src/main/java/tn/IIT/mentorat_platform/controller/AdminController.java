package tn.IIT.mentorat_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.IIT.mentorat_platform.dto.response.StatistiquesResponse;
import tn.IIT.mentorat_platform.dto.response.TitreProfessionnelResponse;
import tn.IIT.mentorat_platform.dto.response.UtilisateurResponse;
import tn.IIT.mentorat_platform.enums.StatutVerification;
import tn.IIT.mentorat_platform.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Administration", description = "Gestion globale de la plateforme (Modération, KPIs, Titres)")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Lister tous les utilisateurs", description = "Retourne la liste complète des étudiants et alumni.")
    @GetMapping("/users")
    public ResponseEntity<List<UtilisateurResponse>> listerUtilisateurs() {
        return ResponseEntity.ok(adminService.listerUtilisateurs());
    }

    @Operation(summary = "Lister tous les mentors", description = "Retourne la liste des alumni disponibles pour le mentorat.")
    @GetMapping("/mentors")
    public ResponseEntity<List<UtilisateurResponse>> listerMentors() {
        return ResponseEntity.ok(adminService.listerMentors());
    }

    @Operation(summary = "Supprimer un compte", description = "Supprime définitivement un utilisateur et toutes ses données associées.")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable Long id) {
        adminService.supprimerUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lister les titres en attente", description = "Retourne les titres professionnels nécessitant une validation manuelle.")
    @GetMapping("/titres/en-attente")
    public ResponseEntity<List<TitreProfessionnelResponse>> listerTitresEnAttente() {
        return ResponseEntity.ok(adminService.listerTitresEnAttente());
    }

    @Operation(summary = "Valider/Rejeter un titre", description = "Modifie le statut de vérification d'une preuve de titre ou diplôme.")
    @PatchMapping("/titres/{id}/valider")
    public ResponseEntity<TitreProfessionnelResponse> validerTitre(
            @PathVariable Long id,
            @RequestParam StatutVerification statut) {
        return ResponseEntity.ok(adminService.validerTitre(id, statut));
    }

    @Operation(summary = "Statistiques Globales", description = "Retourne les KPIs pour le dashboard Administrateur.")
    @GetMapping("/stats")
    public ResponseEntity<StatistiquesResponse> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @Operation(summary = "Lister demandes mentorat", description = "Retourne les alumni ayant soumis une demande pour devenir mentor.")
    @GetMapping("/demandes-mentor")
    public ResponseEntity<java.util.List<tn.IIT.mentorat_platform.dto.response.DemandesMentorResponse>> listerDemandesMentor() {
        return ResponseEntity.ok(adminService.listerDemandesMentor());
    }

    @Operation(summary = "Traiter une demande mentorat", description = "Approuve ou refuse la demande d'un alumni pour devenir mentor.")
    @PatchMapping("/demandes-mentor/{alumniId}")
    public ResponseEntity<Void> traiterDemandeMentor(
            @PathVariable Long alumniId,
            @RequestParam boolean approuver) {
        adminService.traiterDemandeMentor(alumniId, approuver);
        return ResponseEntity.noContent().build();
    }
}
