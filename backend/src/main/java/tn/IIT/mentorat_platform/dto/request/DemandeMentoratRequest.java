package tn.IIT.mentorat_platform.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DemandeMentoratRequest {

    @NotNull(message = "L'identifiant de l'alumni est obligatoire")
    private Long alumniId;

    private String message; // Message d'introduction (optionnel)
}
