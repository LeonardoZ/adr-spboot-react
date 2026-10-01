package com.example.adrmanager.adr;

import java.time.Instant;

public record AdrResponse(String identifier, String adlIdentifier, String title, String context, String problem,
		String optionsConsidered, String decision, String consequences, AdrStatus status, String authorUserId,
		String authorDisplayName, Instant createdAt, String submittedByUserId, Instant submittedAt,
		String decidedByUserId, String decidedByDisplayName, Instant decidedAt, String decisionComment,
		String rejectionJustification, long version) {
	static AdrResponse from(Adr adr) {
		return new AdrResponse(adr.getBusinessIdentifier(), adr.getAdl().getBusinessIdentifier(), adr.getTitle(),
				adr.getContext(), adr.getProblem(), adr.getOptionsConsidered(), adr.getDecision(),
				adr.getConsequences(), adr.getStatus(), adr.getAuthorUserId(), adr.getAuthorDisplayName(),
				adr.getCreatedAt(), adr.getSubmittedByUserId(), adr.getSubmittedAt(), adr.getDecidedByUserId(),
				adr.getDecidedByDisplayName(), adr.getDecidedAt(), adr.getDecisionComment(),
				adr.getRejectionJustification(), adr.getVersion());
	}
}
