package com.netsentinel.config;

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
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI netSentinelOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NetSentinel SIEM API")
                        .description("""
                                **NetSentinel** es un SIEM ligero (Security Information and Event Management) \
                                que permite la ingesta de eventos de seguridad y la detección automática de \
                                patrones sospechosos mediante un motor de reglas.
                                
                                ## Autenticación
                                1. Llama a `POST /api/auth/login` con `username: admin` / `password: admin123`.
                                2. Copia el `token` de la respuesta.
                                3. Haz clic en el botón **Authorize 🔒** (arriba a la derecha).
                                4. Escribe `Bearer <tu_token>` y confirma.
                                5. ¡Ya puedes lanzar peticiones autenticadas desde aquí!
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("NetSentinel Team")
                                .email("admin@netsentinel.local"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                // Registrar el esquema de seguridad JWT
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Introduce tu token JWT con el prefijo **Bearer**. Ejemplo: `Bearer eyJ...`")))
                // Aplicar seguridad JWT a todos los endpoints por defecto
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
