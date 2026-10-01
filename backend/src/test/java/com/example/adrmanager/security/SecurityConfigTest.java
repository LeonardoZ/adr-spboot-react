package com.example.adrmanager.security;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SecurePingController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private JwtDecoder jwtDecoder;

	@Test
	void rejectsMissingToken() throws Exception {
		mockMvc.perform(get("/api/ping")).andExpect(status().isUnauthorized());
	}

	@Test
	void rejectsInvalidToken() throws Exception {
		when(jwtDecoder.decode("invalid")).thenThrow(new BadJwtException("invalid token"));

		mockMvc.perform(get("/api/ping").header("Authorization", "Bearer invalid"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void rejectsUnrecognizedRole() throws Exception {
		when(jwtDecoder.decode("unknown-role")).thenReturn(jwtWithRealmRole("UNKNOWN"));

		mockMvc.perform(get("/api/ping").header("Authorization", "Bearer unknown-role"))
			.andExpect(status().isForbidden());
	}

	@Test
	void acceptsRecognizedRealmRole() throws Exception {
		when(jwtDecoder.decode("user-role")).thenReturn(jwtWithRealmRole("USER"));

		mockMvc.perform(get("/api/ping").header("Authorization", "Bearer user-role")).andExpect(status().isOk());
	}

	private Jwt jwtWithRealmRole(String role) {
		Instant now = Instant.now();
		return Jwt.withTokenValue("token")
			.header("alg", "none")
			.subject("test-user")
			.issuedAt(now)
			.expiresAt(now.plusSeconds(300))
			.claim("realm_access", Map.of("roles", List.of(role)))
			.build();
	}

}
