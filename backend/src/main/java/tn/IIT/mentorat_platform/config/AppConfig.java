package tn.IIT.mentorat_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    /**
     * RestTemplate pour les appels vers le service Python IA.
     * Configuré sans timeout ici ; les timeouts sont gérés au niveau du service.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
