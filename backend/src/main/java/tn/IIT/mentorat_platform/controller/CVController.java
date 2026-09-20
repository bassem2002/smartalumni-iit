package tn.IIT.mentorat_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.IIT.mentorat_platform.dto.response.GestionnaireCVResponse;
import tn.IIT.mentorat_platform.service.CVService;

@RestController
@RequestMapping("/api/cv")
@RequiredArgsConstructor
@Tag(name = "CV", description = "Gestion de l'import et de l'extraction de texte (Apache Tika)")
@SecurityRequirement(name = "BearerAuth")
public class CVController {

    private final CVService cvService;

    @Operation(summary = "Uploader un CV", description = "Sauvegarde un fichier PDF/Word localement et extrait son texte via Tika.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GestionnaireCVResponse> uploadCV(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(cvService.uploadCV(authentication.getName(), file));
    }

    @Operation(summary = "Consulter les infos du CV", description = "Retourne les métadonnées du CV enregistré pour l'utilisateur connecté.")
    @GetMapping("/me")
    public ResponseEntity<GestionnaireCVResponse> getMyCV(Authentication authentication) {
        return ResponseEntity.ok(cvService.getMyCV(authentication.getName()));
    }

    @Operation(summary = "Consulter un CV par ID")
    @GetMapping("/{cvId}")
    public ResponseEntity<GestionnaireCVResponse> getCV(
            Authentication authentication,
            @PathVariable Long cvId) {
        return ResponseEntity.ok(cvService.getCV(cvId, authentication.getName()));
    }

    @Operation(summary = "Lancer l'analyse intelligente (DeepSeek)", description = "Analyse le texte déjà extrait via DeepSeek ou fallback local.")
    @PostMapping("/{cvId}/analyze")
    public ResponseEntity<GestionnaireCVResponse> analyzeCV(
            Authentication authentication,
            @PathVariable Long cvId) {
        return ResponseEntity.ok(cvService.analyserCV(cvId, authentication.getName()));
    }

    @Operation(summary = "Mapper les données détectées vers le profil")
    @PatchMapping("/{cvId}/mapper-profil")
    public ResponseEntity<GestionnaireCVResponse> mapperProfil(
            Authentication authentication,
            @PathVariable Long cvId,
            @RequestBody(required = false) java.util.Map<String, String> requestBody) {
        return ResponseEntity.ok(cvService.mapperProfil(cvId, authentication.getName(), requestBody));
    }

    @Operation(summary = "Supprimer un CV")
    @DeleteMapping("/{cvId}")
    public ResponseEntity<Void> deleteCV(
            Authentication authentication,
            @PathVariable Long cvId) {
        cvService.deleteCV(cvId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
