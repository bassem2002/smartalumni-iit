package tn.IIT.mentorat_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.IIT.mentorat_platform.enums.StatutVerification;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TitreProfessionnelResponse {
    private Long id;
    private String intitule;
    private String organismeDelivrant;
    private LocalDate dateObtention;
    private StatutVerification statutVerification;
    private String preuveDocumentaire;
}
