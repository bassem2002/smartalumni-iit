package tn.IIT.mentorat_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private Boolean lu;
    private Long expediteurId;
    private String expediteurNom;
    private String expediteurPrenom;
    private String expediteurPhoto;
    private String fileUrl;
    private String fileType;
}
