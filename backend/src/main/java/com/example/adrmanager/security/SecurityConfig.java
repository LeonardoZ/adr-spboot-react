package com.example.adrmanager.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.example.adrmanager.api.ApiProblem;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private static final Set<String> RECOGNIZED_ROLES = Set.of("USER");

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
		return http.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/actuator/health", "/actuator/info", "/api-docs/**", "/swagger-ui/**")
				.permitAll()
				.requestMatchers("/api/**")
				.hasRole("USER")
				.anyRequest()
				.denyAll())
			.exceptionHandling(errors -> errors
				.authenticationEntryPoint((request, response, exception) -> writeProblem(response, objectMapper, 401,
						"AUTHENTICATION_REQUIRED", "Authentication is required"))
				.accessDeniedHandler((request, response, exception) -> writeProblem(response, objectMapper, 403,
						"ACCESS_DENIED", "You are not allowed to perform this action")))
			.oauth2ResourceServer(
					oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
			.build();
	}

	private void writeProblem(HttpServletResponse response, ObjectMapper objectMapper, int status, String code,
			String message) throws java.io.IOException {
		response.setStatus(status);
		response.setContentType("application/problem+json");
		objectMapper.writeValue(response.getOutputStream(),
				new ApiProblem(java.time.Instant.now(), status, code, message, Map.of()));
	}

	@Bean
	Converter<Jwt, ? extends org.springframework.security.authentication.AbstractAuthenticationToken> jwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(jwt -> Stream.concat(realmRoles(jwt), clientRoles(jwt))
			.filter(RECOGNIZED_ROLES::contains)
			.<GrantedAuthority>map(
					role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role))
			.toList());
		return converter;
	}

	@SuppressWarnings("unchecked")
	private Stream<String> realmRoles(Jwt jwt) {
		Object realmAccess = jwt.getClaim("realm_access");
		if (!(realmAccess instanceof Map<?, ?> access)) {
			return Stream.empty();
		}
		Object roles = access.get("roles");
		return roles instanceof Collection<?> values
				? values.stream().filter(String.class::isInstance).map(String.class::cast) : Stream.empty();
	}

	@SuppressWarnings("unchecked")
	private Stream<String> clientRoles(Jwt jwt) {
		Object resourceAccess = jwt.getClaim("resource_access");
		if (!(resourceAccess instanceof Map<?, ?> resources)) {
			return Stream.empty();
		}
		return List.of("adr-manager-spa", "adr-manager-api")
			.stream()
			.map(resources::get)
			.filter(Map.class::isInstance)
			.map(Map.class::cast)
			.map(client -> client.get("roles"))
			.filter(Collection.class::isInstance)
			.flatMap(roles -> ((Collection<?>) roles).stream())
			.filter(String.class::isInstance)
			.map(String.class::cast);
	}

}
