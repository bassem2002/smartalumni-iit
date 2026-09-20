package tn.IIT.mentorat_platform.service;

import tn.IIT.mentorat_platform.dto.request.DemandeMentoratRequest;
import tn.IIT.mentorat_platform.dto.request.StatutUpdateRequest;
import tn.IIT.mentorat_platform.dto.response.DemandeMentoratResponse;

import java.util.List;

public interface MentoratService {
    DemandeMentoratResponse envoyerDemande(String email, DemandeMentoratRequest request);
    DemandeMentoratResponse changerStatut(String email, Long demandeId, StatutUpdateRequest request);
    List<DemandeMentoratResponse> getMesDemandes(String email);
}
