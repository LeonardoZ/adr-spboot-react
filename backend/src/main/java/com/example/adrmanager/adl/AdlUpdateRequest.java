package com.example.adrmanager.adl;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import io.swagger.v3.oas.annotations.media.Schema;

public record AdlUpdateRequest(@NotBlank(message = "title is required") @Size(max = 255) String title,
		@NotBlank(message = "context is required") String context,
		@NotBlank(message = "problem is required") String problem,
		@Size(max = 64) Set<@NotBlank @Size(max = 64) String> tags,
		@Schema(description = "Current optimistic-lock version",
				example = "3") @NotNull(message = "version is required") Long version) {
}
