package com.example.adrmanager.adl;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record AdlCreateRequest(@NotBlank(message = "title is required") @Size(max = 255) String title,
		@NotBlank(message = "context is required") String context,
		@NotBlank(message = "problem is required") String problem,
		@Size(max = 64) Set<@NotBlank @Size(max = 64) String> tags) {
}
