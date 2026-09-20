package tn.IIT.mentorat_platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageRequest {

    @NotBlank(message = "Le contenu du message ne peut pas être vide")
    private String contenu;
}
