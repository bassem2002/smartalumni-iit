package tn.IIT.mentorat_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origines autorisées (Angular dev + prod)
        configuration.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://localhost:3000"
        ));

        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // En-têtes autorisés
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Cache-Control"
        ));

        // Exposer le header Authorization dans les réponses
        configuration.setExposedHeaders(List.of("Authorization"));

        // Autoriser les credentials (cookies, JWT headers)
        configuration.setAllowCredentials(true);

        // Cache preflight pour 1 heure
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
