package com.example.adrmanager.api;

import java.time.Instant;
import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard problem response returned for validation, authentication, authorization, missing-resource, and conflict errors")
public record ApiProblem(Instant timestamp, int status, @Schema(example = "CONFLICT") String code,
		@Schema(example = "The supplied version is stale") String message, Map<String, String> fieldErrors) {
}
