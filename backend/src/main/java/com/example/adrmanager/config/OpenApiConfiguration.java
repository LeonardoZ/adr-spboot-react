package com.example.adrmanager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

	@Bean
	OpenAPI adrManagerOpenApi() {
		return new OpenAPI()
			.info(new Info().title("ADR Manager API")
				.version("v1")
				.description("Company-wide Architecture Decision Log and Record register."))
			.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
			.components(new Components().addSecuritySchemes("bearerAuth",
					new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
	}

}
