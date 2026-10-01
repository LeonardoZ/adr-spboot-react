package com.example.adrmanager.adr;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdrCreateRequest(@NotBlank(message = "title is required") @Size(max = 255) String title,
		@NotBlank(message = "context is required") String context,
		@NotBlank(message = "problem is required") String problem, String optionsConsidered, String decision,
		String consequences) {
}
