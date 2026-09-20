package tn.IIT.mentorat_platform.service;

import tn.IIT.mentorat_platform.dto.response.AiCvExtractionResponse;

public interface AiCvExtractionService {
    AiCvExtractionResponse extractFromText(String cvText);
}
