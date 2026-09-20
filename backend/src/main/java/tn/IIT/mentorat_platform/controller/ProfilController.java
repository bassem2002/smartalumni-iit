package tn.IIT.mentorat_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.IIT.mentorat_platform.dto.request.ProfilUpdateRequest;
import tn.IIT.mentorat_platform.dto.response.ProfilResponse;
import tn.IIT.mentorat_platform.service.ProfilService;

@RestController
@RequestMapping("/api/profils")
@RequiredArgsConstructor
@Tag(name = "Profil", description = "Gestion du profil de l'utilisateur connecté")
@SecurityRequirement(name = "BearerAuth")
public class ProfilController {

    private final ProfilService profilService;

    @Operation(summary = "Consulter son profil", description = "Récupère le profil de l'utilisateur authentifié.")
    @GetMapping("/me")
    public ResponseEntity<ProfilResponse> getMyProfil(Authentication authentication) {
        return ResponseEntity.ok(profilService.getMyProfil(authentication.getName()));
    }

    @Operation(summary = "Mettre à jour son profil", description = "Modifie les informations du profil de l'utilisateur authentifié.")
    @PutMapping("/me")
    public ResponseEntity<ProfilResponse> updateProfil(
            Authentication authentication,
            @RequestBody ProfilUpdateRequest request) {
        return ResponseEntity.ok(profilService.updateProfil(authentication.getName(), request));
    }

    @Operation(summary = "Ajouter un titre professionnel", description = "Ajoute un nouveau titre/diplôme au profil.")
    @PostMapping("/me/titres")
    public ResponseEntity<ProfilResponse> addTitre(
            Authentication authentication,
            @RequestBody tn.IIT.mentorat_platform.dto.request.TitreProfessionnelRequest request) {
        return ResponseEntity.ok(profilService.addTitre(authentication.getName(), request));
    }

    @Operation(summary = "Supprimer un titre", description = "Supprime un titre professionnel du profil.")
    @DeleteMapping("/me/titres/{titreId}")
    public ResponseEntity<ProfilResponse> deleteTitre(
            Authentication authentication,
            @PathVariable Long titreId) {
        return ResponseEntity.ok(profilService.deleteTitre(authentication.getName(), titreId));
    }

    @Operation(summary = "Changer le mot de passe", description = "Modifie le mot de passe de l'utilisateur authentifiǸ.")
    @PostMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @RequestBody @jakarta.validation.Valid tn.IIT.mentorat_platform.dto.request.ChangePasswordRequest request) {
        profilService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Soumettre demande mentorat", description = "Permet à un alumni de soumettre une demande pour devenir mentor.")
    @PostMapping("/me/demande-mentor")
    public ResponseEntity<Void> soumettreDemandeDevenir(Authentication authentication) {
        profilService.soumettreDemandeDevenir(authentication.getName());
        return ResponseEntity.noContent().build();
    }

}
