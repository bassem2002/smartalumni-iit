package tn.IIT.mentorat_platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TitreProfessionnelRequest {

    @NotBlank(message = "L'intitulé du titre est obligatoire")
    private String intitule;

    private String organismeDelivrant;

    private LocalDate dateObtention;

    /** Chemin du fichier de preuve (fourni après upload) */
    private String preuveDocumentaire;
}
