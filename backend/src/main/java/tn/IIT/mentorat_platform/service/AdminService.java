package tn.IIT.mentorat_platform.service;

import tn.IIT.mentorat_platform.dto.response.DemandesMentorResponse;
import tn.IIT.mentorat_platform.dto.response.StatistiquesResponse;
import tn.IIT.mentorat_platform.dto.response.TitreProfessionnelResponse;
import tn.IIT.mentorat_platform.dto.response.UtilisateurResponse;
import tn.IIT.mentorat_platform.enums.StatutVerification;

import java.util.List;

public interface AdminService {
    List<UtilisateurResponse> listerUtilisateurs();
    void supprimerUtilisateur(Long utilisateurId);
    
    List<TitreProfessionnelResponse> listerTitresEnAttente();
    TitreProfessionnelResponse validerTitre(Long titreId, StatutVerification statut);
    
    StatistiquesResponse getDashboardStats();

    List<DemandesMentorResponse> listerDemandesMentor();

    void traiterDemandeMentor(Long alumniId, boolean approuver);

    List<UtilisateurResponse> listerMentors();
}
