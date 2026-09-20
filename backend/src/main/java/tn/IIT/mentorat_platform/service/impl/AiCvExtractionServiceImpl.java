package tn.IIT.mentorat_platform.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.IIT.mentorat_platform.dto.response.AiCvExtractionResponse;
import tn.IIT.mentorat_platform.exception.BusinessException;
import tn.IIT.mentorat_platform.service.AiCvExtractionService;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiCvExtractionServiceImpl implements AiCvExtractionService {

    @Value("${ai.deepseek.enabled:true}")
    private boolean enabled;

    @Value("${ai.deepseek.api-key:}")
    private String apiKey;

    @Value("${ai.deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${ai.deepseek.model:deepseek-chat}")
    private String model;

    @Value("${ai.deepseek.timeout-seconds:30}")
    private int timeoutSeconds;

    private final ObjectMapper objectMapper;
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutSeconds * 1000);
        factory.setReadTimeout(timeoutSeconds * 1000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AiCvExtractionResponse extractFromText(String cvText) {
        if (!enabled) {
            throw new BusinessException("Service d'analyse IA désactivé");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException("Clé API DeepSeek non configurée");
        }
        if (cvText == null || cvText.isBlank()) {
            throw new BusinessException("Le texte du CV est vide");
        }

        try {
            String url = baseUrl + "/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String prompt = """
Tu es un extracteur de CV.

À partir du texte brut du CV fourni, extrais uniquement les informations présentes dans le CV.

Retourne uniquement un JSON valide, sans texte avant ou après.

Ne crée pas d’informations absentes du CV.

Si une information n’existe pas, retourne null ou une liste vide.

Champs attendus :

{
  "posteDetecte": string ou null,
  "entrepriseDetectee": string ou null,
  "secteurDetecte": string ou null,
  "paysDetecte": string ou null,
  "competencesDetectees": string[],
  "experiencesDetectees": string[],
  "formationsDetectees": string[],
  "certificationsDetectees": string[]
}

Règles :
- Ne pas inventer.
- Ne pas résumer tout le CV.
- Extraire des valeurs courtes et propres.
- Pour les expériences, garder seulement l’entreprise, le type d’expérience, la période et éventuellement le poste.
- Pour les formations, garder le diplôme, l’établissement et la période si disponible.
- Pour les compétences, extraire les technologies, langages, frameworks, bases de données et outils.
- Le CV peut être en français ou en anglais.

Texte du CV :
""" + cvText;

            // Prepare Request Body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
            ));
            
            // Enable JSON mode
            requestBody.put("response_format", Map.of("type", "json_object"));
            requestBody.put("stream", false);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Appel API DeepSeek model={} url={}", model, url);
            
            // Invoke the API
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            
            if (response == null || !response.containsKey("choices")) {
                throw new BusinessException("Réponse vide de la part de l'API DeepSeek");
            }

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new BusinessException("Aucun choix retourné par l'API DeepSeek");
            }

            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            if (message == null || !message.containsKey("content")) {
                throw new BusinessException("Aucun contenu de message retourné par l'API DeepSeek");
            }

            String jsonResponse = (String) message.get("content");
            if (jsonResponse == null || jsonResponse.isBlank()) {
                throw new BusinessException("Contenu de la réponse IA vide");
            }

            log.debug("Réponse JSON brute reçue de DeepSeek : {}", jsonResponse);

            // Parse JSON into DTO
            return objectMapper.readValue(jsonResponse, AiCvExtractionResponse.class);

        } catch (Exception e) {
            log.error("Erreur lors de l'appel de l'API DeepSeek: {}", e.getMessage());
            throw new BusinessException("Erreur lors de l'extraction IA: " + e.getMessage());
        }
    }
}
