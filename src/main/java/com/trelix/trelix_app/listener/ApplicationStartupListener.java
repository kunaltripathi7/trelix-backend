package com.trelix.trelix_app.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(ApplicationStartupListener.class);

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");

        String swaggerUiUrl = String.format("http://localhost:%s%s/swagger-ui.html", port, contextPath);
        String apiDocsUrl = String.format("http://localhost:%s%s/v3/api-docs", port, contextPath);

        log.info("========================================================================================");
        log.info("  Application '{}' is running!", env.getProperty("spring.application.name"));
        log.info("  Swagger UI: {}", swaggerUiUrl);
        log.info("  API Docs:   {}", apiDocsUrl);
        log.info("========================================================================================");
    }
}
