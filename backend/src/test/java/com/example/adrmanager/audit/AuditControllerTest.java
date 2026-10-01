package com.example.adrmanager.audit;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.adrmanager.adl.AdlRepository;
import com.example.adrmanager.adr.AdrRepository;
import com.example.adrmanager.api.ApiExceptionHandler;
import com.example.adrmanager.security.SecurityConfig;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuditController.class)
@Import({ SecurityConfig.class, ApiExceptionHandler.class })
class AuditControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuditEventRepository events;

	@MockBean
	private AdlRepository adls;

	@MockBean
	private AdrRepository adrs;

	@MockBean
	private JwtDecoder jwtDecoder;

	@Test
	void authenticatedUserGetsChronologicalAdlHistoryIncludingArchivedRecords() throws Exception {
		authenticate("reader", "USER");
		when(adls.findByBusinessIdentifier("ADL-1")).thenReturn(Optional.of(anyAdl()));
		when(events.findByEntityTypeAndEntityIdentifierOrderByOccurredAtAsc(AuditEntityType.ADL, "ADL-1"))
			.thenReturn(List.of(event("CREATED", "2026-01-01T10:00:00Z", null, "{\"status\":\"OPEN\"}"),
					event("ARCHIVED", "2026-02-01T10:00:00Z", "{\"status\":\"OPEN\"}", "{\"status\":\"ARCHIVED\"}")));

		mockMvc.perform(get("/api/adls/ADL-1/audit-events").header("Authorization", "Bearer reader"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].action").value("CREATED"))
			.andExpect(jsonPath("$[0].occurredAt").value("2026-01-01T10:00:00Z"))
			.andExpect(jsonPath("$[1].action").value("ARCHIVED"))
			.andExpect(jsonPath("$[1].beforeValue").value("{\"status\":\"OPEN\"}"))
			.andExpect(jsonPath("$[1].afterValue").value("{\"status\":\"ARCHIVED\"}"));
	}

	@Test
	void anonymousUserCannotReadAuditHistory() throws Exception {
		mockMvc.perform(get("/api/adrs/ADR-1/audit-events"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));
	}

	private AuditEvent event(String action, String occurredAt, String before, String after) {
		return new AuditEvent(AuditEntityType.ADL, "ADL-1", action, "architect-1", "Architect One",
				Instant.parse(occurredAt), before, after);
	}

	private com.example.adrmanager.adl.Adl anyAdl() {
		return new com.example.adrmanager.adl.Adl("ADL-1", "Architecture", "Context", "Problem", "architect",
				"Architect", Instant.parse("2026-01-01T00:00:00Z"));
	}

	private void authenticate(String token, String role) {
		Instant now = Instant.now();
		when(jwtDecoder.decode(token)).thenReturn(Jwt.withTokenValue(token)
			.header("alg", "none")
			.subject(token)
			.issuedAt(now)
			.expiresAt(now.plusSeconds(300))
			.claim("realm_access", Map.of("roles", List.of(role)))
			.build());
	}

}
