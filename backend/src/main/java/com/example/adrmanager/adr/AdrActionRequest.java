package com.example.adrmanager.adr;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

public record AdrActionRequest(@Schema(description = "Current optimistic-lock version",
		example = "2") @NotNull(message = "version is required") Long version) {
}
