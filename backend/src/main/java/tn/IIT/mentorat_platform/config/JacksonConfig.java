package tn.IIT.mentorat_platform.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Optionnel : Ajout du support pour les types Java 8 Time (LocalDateTime, etc.)
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
