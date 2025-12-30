package juste.chambrepro.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(
                        new Info()
                                .title("ChambrePro Backend API")
                                .description(
                                        "API de plateforme de reservation de chambre")
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("PAKOU Komi Juste")
                                                .email("pakoujuste@gmail.com")
                                                .url("https://justeportfolio"))
                                .license(new License().name("Proprietary").url("https://ChambrePro.com/license")))
                .servers(
                        List.of(
                                new Server().url("http://localhost:8080").description("Serveur de développement"),
                                new Server()
                                        .url("https://chambrepro-backend-springboot.onrender.com")
                                        .description("Serveur de production")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description(
                                                        "Entrez votre token JWT. Format: Bearer {token}\n\n"
                                                                + "Pour obtenir un token:\n"
                                                                + "1. Utilisez l'endpoint POST /user/login avec vos identifiants\n"
                                                                + "2. Copiez le token reçu dans la réponse\n"
                                                                + "3. Cliquez sur le bouton 'Authorize' (cadenas) ci-dessus\n"
                                                                + "4. Collez le token dans le champ 'Value'\n"
                                                                + "5. Cliquez sur 'Authorize' puis 'Close'\n\n"
                                                                + "Le token sera automatiquement ajouté à toutes les requêtes.")));
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            // Crée un schéma réutilisable pour un tableau de fichiers binaires
            var filesSchema = new io.swagger.v3.oas.models.media.Schema<List<String>>()
                    .type("array")
                    .items(new io.swagger.v3.oas.models.media.Schema<String>().type("string").format("binary"));

            // Ajoute ce schéma aux composants OpenAPI sous le nom 'files'
            openApi.getComponents().addSchemas("files", filesSchema);
        };
    }
}
