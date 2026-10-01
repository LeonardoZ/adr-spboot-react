package com.example.adrmanager.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "spring.datasource.url=jdbc:h2:mem:adr-manager-health;MODE=MariaDB;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:9999/keys" })
@AutoConfigureMockMvc
class OpenApiAndHealthIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void publishesSecuredOpenApiDocument() throws Exception {
		mockMvc.perform(get("/api-docs"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
			.andExpect(jsonPath("$.security[0].bearerAuth").exists())
			.andExpect(jsonPath("$.paths['/api/adls'].get.parameters[?(@.name == 'page')].description")
				.value("Zero-based page"))
			.andExpect(jsonPath("$.paths['/api/adls'].get.parameters[?(@.name == 'sort')].example")
				.value("createdAt,desc"))
			.andExpect(jsonPath("$.paths['/api/adrs/{identifier}/approve'].post.responses.409.description")
				.value("Stale version or ADR is not under review"));
	}

	@Test
	void reportsDatabaseDependencyInHealthPayload() throws Exception {
		mockMvc.perform(get("/actuator/health"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("UP"))
			.andExpect(jsonPath("$.components.db.status").value("UP"));
	}

}
