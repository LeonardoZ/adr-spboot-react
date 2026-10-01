package com.example.adrmanager.identity;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
class RestKeycloakAdminGateway implements KeycloakAdminGateway {

	private final RestClient restClient;

	private final KeycloakProperties properties;

	RestKeycloakAdminGateway(RestClient.Builder builder, KeycloakProperties properties) {
		this.restClient = builder.baseUrl(properties.baseUrl()).build();
		this.properties = properties;
	}

	@Override
	public Optional<KeycloakUser> findById(String userId) {
		try {
			String accessToken = requestServiceToken();
			Map<?, ?> user = restClient.get()
				.uri("/admin/realms/{realm}/users/{userId}", properties.realm(), userId)
				.headers(headers -> headers.setBearerAuth(accessToken))
				.retrieve()
				.body(Map.class);
			return Optional.of(toUser(user));
		}
		catch (RestClientResponseException exception) {
			if (exception.getStatusCode().value() == 404) {
				return Optional.empty();
			}
			throw new KeycloakUnavailableException("Keycloak user lookup failed", exception);
		}
		catch (KeycloakUnavailableException exception) {
			throw exception;
		}
		catch (RuntimeException exception) {
			throw new KeycloakUnavailableException("Keycloak user lookup is unavailable", exception);
		}
	}

	private String requestServiceToken() {
		Map<?, ?> token = restClient.post()
			.uri("/realms/{realm}/protocol/openid-connect/token", properties.realm())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body("grant_type=client_credentials&client_id=" + properties.clientId() + "&client_secret="
					+ properties.clientSecret())
			.retrieve()
			.body(Map.class);
		Object accessToken = token == null ? null : token.get("access_token");
		if (!(accessToken instanceof String value) || value.isBlank()) {
			throw new KeycloakUnavailableException("Keycloak returned no service access token", null);
		}
		return value;
	}

	private KeycloakUser toUser(Map<?, ?> user) {
		String username = value(user, "username");
		String firstName = value(user, "firstName");
		String lastName = value(user, "lastName");
		String displayName = String.join(" ", firstName, lastName).trim();
		if (displayName.isBlank()) {
			displayName = username;
		}
		return new KeycloakUser(value(user, "id"), username, displayName, Boolean.TRUE.equals(user.get("enabled")));
	}

	private String value(Map<?, ?> values, String name) {
		Object value = values.get(name);
		return value instanceof String string ? string : "";
	}

}
