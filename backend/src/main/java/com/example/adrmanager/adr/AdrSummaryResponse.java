package com.example.adrmanager.adr;

import java.time.Instant;

public record AdrSummaryResponse(String identifier, String title, AdrStatus status, String authorDisplayName,
		Instant createdAt) {
}
