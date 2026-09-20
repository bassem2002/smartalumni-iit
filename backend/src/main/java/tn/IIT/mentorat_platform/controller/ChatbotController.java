package tn.IIT.mentorat_platform.controller;

import org.springframework.web.bind.annotation.*;
import tn.IIT.mentorat_platform.service.ChatbotService;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/chat")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/ask")
    public Map<String, String> askChatbot(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String reponse = chatbotService.interrogerIA(question);
        return Map.of("reponse", reponse);
    }
}
