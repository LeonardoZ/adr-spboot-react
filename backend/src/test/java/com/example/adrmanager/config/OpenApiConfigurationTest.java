package com.example.adrmanager.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OpenApiConfigurationTest {

	@Test
	void publishesGlobalBearerSecurityScheme() {
		var openApi = new OpenApiConfiguration().adrManagerOpenApi();

		assertEquals("bearer", openApi.getComponents().getSecuritySchemes().get("bearerAuth").getScheme());
		assertTrue(openApi.getSecurity().stream().anyMatch(requirement -> requirement.containsKey("bearerAuth")));
	}

}
