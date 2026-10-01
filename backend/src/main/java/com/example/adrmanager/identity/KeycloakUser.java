package com.example.adrmanager.identity;

public record KeycloakUser(String id, String username, String displayName, boolean enabled) {
}
