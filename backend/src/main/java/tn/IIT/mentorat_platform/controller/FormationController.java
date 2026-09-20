package tn.IIT.mentorat_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.IIT.mentorat_platform.service.FormationScraperService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
@Tag(name = "Formations", description = "Données dynamiques extraites depuis le site de l'IIT")
public class FormationController {

    private final FormationScraperService scraperService;

    @Operation(summary = "Obtenir les filières et spécialités", description = "Retourne la liste des filières et leurs spécialités respectives scrapées depuis iit.tn")
    @GetMapping
    public ResponseEntity<Map<String, List<String>>> getFormations() {
        return ResponseEntity.ok(scraperService.getFormationsFromSite());
    }
}
