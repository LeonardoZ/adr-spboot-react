package com.example.adrmanager.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.example.adrmanager.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ApiProbeController.class)
@Import({ SecurityConfig.class, ApiExceptionHandler.class })
class ApiProblemContractTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private JwtDecoder jwtDecoder;

	@Test
	void returnsValidationProblem() throws Exception {
		authenticated("validation");
		mockMvc
			.perform(post("/api/probes").header("Authorization", "Bearer validation")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.fieldErrors.name").value("name is required"));
	}

	@Test
	void returnsMissingResourceProblem() throws Exception {
		authenticated("missing");
		mockMvc.perform(get("/api/probes/missing/42").header("Authorization", "Bearer missing"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
	}

	@Test
	void returnsConflictProblem() throws Exception {
		authenticated("conflict");
		mockMvc.perform(post("/api/probes/conflict").header("Authorization", "Bearer conflict"))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value("CONFLICT"));
	}

	@Test
	void returnsAuthenticationProblem() throws Exception {
		mockMvc.perform(get("/api/probes/missing/42"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));
	}

	@Test
	void returnsAuthorizationProblemForUnknownRole() throws Exception {
		when(jwtDecoder.decode("unknown")).thenReturn(jwt("UNKNOWN"));
		mockMvc.perform(get("/api/probes/missing/42").header("Authorization", "Bearer unknown"))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
	}

	private void authenticated(String token) {
		when(jwtDecoder.decode(token)).thenReturn(jwt("USER"));
	}

	private Jwt jwt(String role) {
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
