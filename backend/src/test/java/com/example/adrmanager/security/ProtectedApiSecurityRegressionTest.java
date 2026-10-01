package com.example.adrmanager.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
		properties = { "spring.datasource.url=jdbc:h2:mem:protected-api-security;MODE=MariaDB;DB_CLOSE_DELAY=-1",
				"spring.datasource.driver-class-name=org.h2.Driver",
				"spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:9999/keys" })
@AutoConfigureMockMvc
class ProtectedApiSecurityRegressionTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private JwtDecoder jwtDecoder;

	@BeforeEach
	void authenticateTokens() {
		org.mockito.Mockito.when(jwtDecoder.decode("user")).thenReturn(jwtWithRole("USER"));
		org.mockito.Mockito.when(jwtDecoder.decode("without-user")).thenReturn(jwtWithRole("LEGACY"));
	}

	@ParameterizedTest
	@MethodSource("protectedActions")
	void everyProtectedActionRejectsAnonymousRequests(HttpMethod method, String path) throws Exception {
		mockMvc.perform(request(method, path))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));
	}

	@ParameterizedTest
	@MethodSource("protectedActions")
	void everyProtectedActionAcceptsUserAuthorization(HttpMethod method, String path) throws Exception {
		mockMvc.perform(request(method, path).header("Authorization", "Bearer user"))
			.andExpect(result -> org.assertj.core.api.Assertions.assertThat(result.getResponse().getStatus())
				.isNotIn(401, 403));
	}

	@ParameterizedTest
	@MethodSource("protectedActions")
	void everyProtectedActionRejectsValidTokenWithoutUser(HttpMethod method, String path) throws Exception {
		mockMvc.perform(request(method, path).header("Authorization", "Bearer without-user"))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
	}

	@Test
	void projectCatalogEndpointIsRemoved() throws Exception {
		mockMvc.perform(request(HttpMethod.GET, "/api/projects").header("Authorization", "Bearer user"))
			.andExpect(status().isNotFound());
	}

	private static Stream<Arguments> protectedActions() {
		return Stream.of(action(HttpMethod.GET, "/api/adls"), action(HttpMethod.POST, "/api/adls"),
				action(HttpMethod.GET, "/api/adls/ADL-1"), action(HttpMethod.PUT, "/api/adls/ADL-1"),
				action(HttpMethod.POST, "/api/adls/ADL-1/archive"),
				action(HttpMethod.GET, "/api/adls/ADL-1/audit-events"), action(HttpMethod.POST, "/api/adls/ADL-1/adrs"),
				action(HttpMethod.GET, "/api/adls/ADL-1/adrs"), action(HttpMethod.GET, "/api/adrs/ADR-1"),
				action(HttpMethod.PUT, "/api/adrs/ADR-1"), action(HttpMethod.POST, "/api/adrs/ADR-1/submit"),
				action(HttpMethod.POST, "/api/adrs/ADR-1/cancel-review"),
				action(HttpMethod.POST, "/api/adrs/ADR-1/approve"), action(HttpMethod.POST, "/api/adrs/ADR-1/reject"),
				action(HttpMethod.GET, "/api/adrs/ADR-1/audit-events"));
	}

	private static Arguments action(HttpMethod method, String path) {
		return Arguments.of(method, path);
	}

	private Jwt jwtWithRole(String role) {
		Instant now = Instant.now();
		return Jwt.withTokenValue(role)
			.header("alg", "none")
			.subject("test-user")
			.issuedAt(now)
			.expiresAt(now.plusSeconds(300))
			.claim("realm_access", Map.of("roles", List.of(role)))
			.build();
	}

}
