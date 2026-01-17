package com.trelix.trelix_app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(info = @Info(title = "Trelix API", version = "1.0.0", description = """
                **Trelix** - Real-Time Team Collaboration Platform

                Backend with:
                - 🔐 JWT Authentication with token rotation
                - 📦 Redis Caching with graceful degradation
                - ⚡ Kafka async messaging with DLQ
                - 🛡️ Rate limiting (100 req/min)
                - 🔌 WebSocket real-time chat
                """, contact = @Contact(name = "Trelix Support", email = "support@trelix.com")), security = @SecurityRequirement(name = "bearerAuth"), servers = {
                @Server(url = "http://localhost:8080/api", description = "Local Development")
}, tags = {
                @Tag(name = "Authentication", description = "Login, Register, Token Refresh"),
                @Tag(name = "Teams", description = "Team management and membership"),
                @Tag(name = "Projects", description = "Project CRUD and member management"),
                @Tag(name = "Tasks", description = "Task management with status tracking"),
                @Tag(name = "Channels", description = "Team chat channels"),
                @Tag(name = "Messages", description = "Channel and direct messages"),
                @Tag(name = "Notifications", description = "In-app notifications")
})
@SecurityScheme(name = "bearerAuth", description = "Enter your JWT token. Get it from POST /v1/auth/login", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {
}
