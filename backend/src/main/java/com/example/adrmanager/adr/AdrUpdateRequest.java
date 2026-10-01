package com.example.adrmanager.adr;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record AdrUpdateRequest(@NotBlank(message = "title is required") @Size(max = 255) String title,
		@NotBlank(message = "context is required") String context,
		@NotBlank(message = "problem is required") String problem, String optionsConsidered, String decision,
		String consequences, @Schema(description = "Current optimistic-lock version",
				example = "2") @NotNull(message = "version is required") Long version) {
}
