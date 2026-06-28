package com.clinix.forge.core.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

class ConfigurationValidatorTest {

    private MockEnvironment environment;
    private ConfigurationValidator validator;

    @BeforeEach
    void setUp() {
        environment = new MockEnvironment();
        validator = new ConfigurationValidator(environment);

        // Set default valid properties
        ReflectionTestUtils.setField(validator, "serverPort", 8080);
        ReflectionTestUtils.setField(validator, "contextPath", "/api/v1");
        ReflectionTestUtils.setField(validator, "datasourceUrl", "jdbc:postgresql://localhost:5432/clinix_data_store");
        ReflectionTestUtils.setField(validator, "datasourceUsername", "postgres");
        ReflectionTestUtils.setField(validator, "jwtSecret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(validator, "jwtExpirationMs", 86400000L);
    }

    @Test
    void testValidConfiguration() {
        assertThatCode(() -> validator.afterPropertiesSet())
                .doesNotThrowAnyException();
    }

    @Test
    void testInvalidServerPort() {
        ReflectionTestUtils.setField(validator, "serverPort", -1);
        assertThatThrownBy(() -> validator.afterPropertiesSet())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("server.port must be between 1 and 65535");

        ReflectionTestUtils.setField(validator, "serverPort", 70000);
        assertThatThrownBy(() -> validator.afterPropertiesSet())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("server.port must be between 1 and 65535");
    }

    @Test
    void testInvalidContextPath() {
        ReflectionTestUtils.setField(validator, "contextPath", "api/v1");
        assertThatThrownBy(() -> validator.afterPropertiesSet())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("server.servlet.context-path must start with '/'");

        ReflectionTestUtils.setField(validator, "contextPath", "");
        assertThatThrownBy(() -> validator.afterPropertiesSet())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("server.servlet.context-path must not be blank");
    }

    @Test
    void testInvalidDatasourceUrl() {
        ReflectionTestUtils.setField(validator, "datasourceUrl", "");
        assertThatThrownBy(() -> validator.afterPropertiesSet())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("spring.datasource.url must not be blank");

        ReflectionTestUtils.setField(validator, "datasourceUrl", "jdbc:mysql://localhost:3306/db");
        assertThatThrownBy(() -> validator.afterPropertiesSet())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("spring.datasource.url must be a valid postgresql or h2 connection string");
    }

    @Test
    void testInvalidDatasourceUsername() {
        ReflectionTestUtils.setField(validator, "datasourceUsername", "");
        assertThatThrownBy(() -> validator.afterPropertiesSet())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("spring.datasource.username must not be blank");
    }

    @Test
    void testInvalidJwtSecret() {
        ReflectionTestUtils.setField(validator, "jwtSecret", "too-short");
        assertThatThrownBy(() -> validator.afterPropertiesSet())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("clinix.security.jwt.secret must be at least 32 characters long");
    }

    @Test
    void testInvalidJwtExpiration() {
        ReflectionTestUtils.setField(validator, "jwtExpirationMs", 0L);
        assertThatThrownBy(() -> validator.afterPropertiesSet())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("clinix.security.jwt.expiration-ms must be a positive number");

        ReflectionTestUtils.setField(validator, "jwtExpirationMs", -5000L);
        assertThatThrownBy(() -> validator.afterPropertiesSet())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("clinix.security.jwt.expiration-ms must be a positive number");
    }

    @Test
    void testProductionProfileChecks() {
        environment.setActiveProfiles("prod");

        // Try dev default secret in production - should fail
        ReflectionTestUtils.setField(validator, "jwtSecret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        assertThatThrownBy(() -> validator.afterPropertiesSet())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot use fallback development credentials in production profile");

        // Try a different secret that is valid
        ReflectionTestUtils.setField(validator, "jwtSecret", "production-only-very-secure-secret-key-12345");
        assertThatCode(() -> validator.afterPropertiesSet())
                .doesNotThrowAnyException();
    }
}
