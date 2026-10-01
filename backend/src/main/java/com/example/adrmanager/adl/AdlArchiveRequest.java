package com.example.adrmanager.adl;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

public record AdlArchiveRequest(@Schema(description = "Current optimistic-lock version",
		example = "3") @NotNull(message = "version is required") Long version) {
}
