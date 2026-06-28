package com.clinix.forge.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Validates critical environment variables and application configurations at startup.
 * If validation fails, prevents application startup with a descriptive error.
 */
@Slf4j
@Component
public class ConfigurationValidator implements InitializingBean {

    private final Environment environment;

    @Value("${server.port:8080}")
    private Integer serverPort;

    @Value("${server.servlet.context-path:/api/v1}")
    private String contextPath;

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @Value("${spring.datasource.username:}")
    private String datasourceUsername;

    @Value("${clinix.security.jwt.secret:}")
    private String jwtSecret;

    @Value("${clinix.security.jwt.expiration-ms:0}")
    private Long jwtExpirationMs;

    public ConfigurationValidator(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Starting configuration validation...");

        List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
        boolean isProd = activeProfiles.contains("prod");

        List<String> errors = new ArrayList<>();

        // 1. Validate server port
        if (serverPort == null || serverPort < 1 || serverPort > 65535) {
            errors.add("server.port must be between 1 and 65535 (current: " + serverPort + ")");
        }

        // 2. Validate server context-path
        if (contextPath == null || contextPath.isBlank()) {
            errors.add("server.servlet.context-path must not be blank");
        } else if (!contextPath.startsWith("/")) {
            errors.add("server.servlet.context-path must start with '/' (current: " + contextPath + ")");
        }

        // 3. Validate datasource settings
        if (datasourceUrl == null || datasourceUrl.isBlank()) {
            errors.add("spring.datasource.url must not be blank");
        } else {
            if (isProd && !datasourceUrl.startsWith("jdbc:postgresql://")) {
                errors.add("spring.datasource.url must start with 'jdbc:postgresql://' in production (current: " + datasourceUrl + ")");
            } else if (!datasourceUrl.startsWith("jdbc:postgresql://") && !datasourceUrl.startsWith("jdbc:h2:")) {
                errors.add("spring.datasource.url must be a valid postgresql or h2 connection string (current: " + datasourceUrl + ")");
            }
        }

        if (datasourceUsername == null || datasourceUsername.isBlank()) {
            errors.add("spring.datasource.username must not be blank");
        }

        // 4. Validate JWT settings
        if (jwtSecret == null || jwtSecret.isBlank()) {
            errors.add("clinix.security.jwt.secret must not be blank");
        } else {
            if (jwtSecret.length() < 32) {
                errors.add("clinix.security.jwt.secret must be at least 32 characters long for security (current length: " + jwtSecret.length() + ")");
            }
            // Check default secrets in production
            String defaultDevSecret1 = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
            String defaultDevSecret2 = "73746f6e655f616e645f737465656c5f6f665f7468655f666f726765";
            if (isProd && (jwtSecret.equals(defaultDevSecret1) || jwtSecret.equals(defaultDevSecret2))) {
                errors.add("clinix.security.jwt.secret cannot use fallback development credentials in production profile");
            }
        }

        if (jwtExpirationMs == null || jwtExpirationMs <= 0) {
            errors.add("clinix.security.jwt.expiration-ms must be a positive number");
        }

        if (!errors.isEmpty()) {
            log.error("Configuration validation failed! Active Profiles: {}", activeProfiles);
            for (String error : errors) {
                log.error("Validation Error: {}", error);
            }
            throw new IllegalStateException("Application startup halted due to configuration errors: " + String.join("; ", errors));
        }

        // Masking password and secrets for logging
        String maskedUrl = datasourceUrl.replaceAll("(:[^@/]+(?=@))|((?<=:)[^/]+(?=//))", "****");
        String maskedSecret = (jwtSecret != null && jwtSecret.length() > 6)
                ? jwtSecret.substring(0, 3) + "..." + jwtSecret.substring(jwtSecret.length() - 3)
                : "******";

        log.info("Configuration validated successfully! Active Profiles: {}", activeProfiles);
        log.info("Server Config: Port={}, Path={}", serverPort, contextPath);
        log.info("Database Config: URL={}, User={}", maskedUrl, datasourceUsername);
        log.info("Security Config: JWT Secret={}, Expiration={}ms", maskedSecret, jwtExpirationMs);
    }
}
