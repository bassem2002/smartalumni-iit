package tn.IIT.mentorat_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.IIT.mentorat_platform.dto.request.LoginRequest;
import tn.IIT.mentorat_platform.dto.request.RegisterRequest;
import tn.IIT.mentorat_platform.dto.response.AuthResponse;
import tn.IIT.mentorat_platform.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints d'inscription et de connexion (Génération du JWT)")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Inscription", description = "Permet de créer un compte Etudiant ou Alumni. Un profil et un CV sont automatiquement générés.")
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestPart("data") RegisterRequest request,
            @RequestPart("cv") MultipartFile cvFile) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request, cvFile));
    }

    @Operation(summary = "Connexion", description = "Authentifie l'utilisateur et retourne un token JWT.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
