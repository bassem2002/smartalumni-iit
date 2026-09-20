package tn.IIT.mentorat_platform.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tn.IIT.mentorat_platform.enums.StatutDemande;

@Data
public class StatutUpdateRequest {

    @NotNull(message = "Le statut est obligatoire")
    private StatutDemande statut;
}
