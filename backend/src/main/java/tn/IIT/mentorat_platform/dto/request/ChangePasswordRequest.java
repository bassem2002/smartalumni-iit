package tn.IIT.mentorat_platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "L'ancien mot de passe est obligatoire")
    private String currentPassword;

    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
    private String newPassword;

    @NotBlank(message = "La confirmation du mot de passe est obligatoire")
    private String confirmPassword;
}
