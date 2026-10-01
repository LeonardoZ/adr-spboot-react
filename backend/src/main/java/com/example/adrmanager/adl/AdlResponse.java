package com.example.adrmanager.adl;

import java.util.Set;
import java.util.List;
import java.time.Instant;
import com.example.adrmanager.adr.AdrSummaryResponse;

public record AdlResponse(String identifier, String title, String context, String problem, Instant archivedAt,
		Set<String> tags, List<AdrSummaryResponse> adrs, long version) {
	static AdlResponse from(Adl adl) {
		return from(adl, List.of());
	}

	static AdlResponse from(Adl adl, List<AdrSummaryResponse> adrs) {
		return new AdlResponse(adl.getBusinessIdentifier(), adl.getTitle(), adl.getContext(), adl.getProblem(),
				adl.getArchivedAt(), adl.getTags(), List.copyOf(adrs), adl.getVersion());
	}
}
