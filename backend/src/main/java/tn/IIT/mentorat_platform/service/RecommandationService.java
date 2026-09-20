package tn.IIT.mentorat_platform.service;

import tn.IIT.mentorat_platform.dto.response.RecommandationIAResponse;

import java.util.List;

public interface RecommandationService {
    List<RecommandationIAResponse> genererRecommandations(String emailEtudiant);
    List<RecommandationIAResponse> getMesRecommandations(String email);
}
