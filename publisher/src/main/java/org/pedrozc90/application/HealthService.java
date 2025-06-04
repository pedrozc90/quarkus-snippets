package org.pedrozc90.application;

import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.pedrozc90.adapters.web.dtos.HealthResponse;
import org.pedrozc90.config.AppConfig;

@ApplicationScoped
public class HealthService {

    @Inject
    protected AppConfig config;

    public HealthResponse create() {
        final String mode = getMode();
        return HealthResponse.builder()
            .app(config.name())
            .mode(mode)
            .online(true)
            .build();
    }

    private String getMode() {
        return ConfigUtils.getProfiles().stream()
            .filter(ConfigUtils::isProfileActive)
            .findFirst()
            .orElse("none");
    }

}
