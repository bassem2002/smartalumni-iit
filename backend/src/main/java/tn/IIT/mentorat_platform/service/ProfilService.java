package tn.IIT.mentorat_platform.service;

import tn.IIT.mentorat_platform.dto.request.ProfilUpdateRequest;
import tn.IIT.mentorat_platform.dto.response.ProfilResponse;

public interface ProfilService {

    ProfilResponse getMyProfil(String email);

    ProfilResponse updateProfil(String email, ProfilUpdateRequest request);

    ProfilResponse addTitre(String email, tn.IIT.mentorat_platform.dto.request.TitreProfessionnelRequest request);

    ProfilResponse deleteTitre(String email, Long titreId);

    void changePassword(String email, tn.IIT.mentorat_platform.dto.request.ChangePasswordRequest request);

    void soumettreDemandeDevenir(String email);
}
