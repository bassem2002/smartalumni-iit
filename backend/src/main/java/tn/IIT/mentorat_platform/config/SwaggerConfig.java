package tn.IIT.mentorat_platform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartAlumni IIT — API REST")
                        .description("""
                                API complète de la plateforme de mentorat Alumni-Étudiants.
                                
                                **Fonctionnalités :**
                                - Authentification JWT (inscription / connexion)
                                - Gestion des profils Alumni et Étudiants
                                - Système de mentorat (demandes, acceptation, chat)
                                - Import et extraction de CV (Apache Tika)
                                - Recommandations IA (service Python externe)
                                - Dashboard administrateur
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("IIT - Institut International de Technologie")
                                .email("admin@iit.tn"))
                        .license(new License().name("Propriétaire IIT"))
                )
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Entrez votre token JWT : Bearer {token}")
                        )
                );
    }
}
