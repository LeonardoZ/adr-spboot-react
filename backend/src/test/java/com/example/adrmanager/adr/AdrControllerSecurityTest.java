package com.example.adrmanager.adr;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.adrmanager.api.ApiExceptionHandler;
import com.example.adrmanager.security.SecurityConfig;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdrController.class)
@Import({ SecurityConfig.class, ApiExceptionHandler.class })
class AdrControllerSecurityTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AdrService service;

	@MockBean
	private JwtDecoder jwtDecoder;

	@Test
	void userCanApproveAndLegacyRoleIsRejected() throws Exception {
		Instant now = Instant.now();
		when(jwtDecoder.decode("user")).thenReturn(Jwt.withTokenValue("user")
			.header("alg", "none")
			.subject("user")
			.issuedAt(now)
			.expiresAt(now.plusSeconds(300))
			.claim("realm_access", Map.of("roles", List.of("USER")))
			.build());
		when(jwtDecoder.decode("legacy")).thenReturn(Jwt.withTokenValue("legacy")
			.header("alg", "none")
			.subject("legacy")
			.issuedAt(now)
			.expiresAt(now.plusSeconds(300))
			.claim("realm_access", Map.of("roles", List.of("APPROVER")))
			.build());

		mockMvc
			.perform(post("/api/adrs/ADR-1/approve").header("Authorization", "Bearer user")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"version\":1}"))
			.andExpect(status().isOk());
		mockMvc
			.perform(post("/api/adrs/ADR-1/approve").header("Authorization", "Bearer legacy")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"version\":1}"))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
	}

}
