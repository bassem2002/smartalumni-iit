package tn.IIT.mentorat_platform.service;

import org.springframework.web.multipart.MultipartFile;
import tn.IIT.mentorat_platform.dto.response.GestionnaireCVResponse;

import java.util.Map;

public interface CVService {
    GestionnaireCVResponse uploadCV(String email, MultipartFile file);
    GestionnaireCVResponse getMyCV(String email);
    GestionnaireCVResponse getCV(Long cvId, String email);
    GestionnaireCVResponse analyserCV(Long cvId, String email);
    GestionnaireCVResponse mapperProfil(Long cvId, String email, Map<String, String> requestBody);
    void deleteCV(Long cvId, String email);
}
