package com.example.adrmanager.identity;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.keycloak")
public record KeycloakProperties(String baseUrl, String realm, String clientId, String clientSecret) {
}
