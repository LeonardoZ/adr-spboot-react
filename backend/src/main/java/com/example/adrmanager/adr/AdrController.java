package com.example.adrmanager.adr;

import com.example.adrmanager.audit.AuditActor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('USER')")
public class AdrController {

	private final AdrService service;

	public AdrController(AdrService service) {
		this.service = service;
	}

	@PostMapping("/adls/{adlIdentifier}/adrs")
	@ResponseStatus(HttpStatus.CREATED)
	public AdrResponse create(@PathVariable String adlIdentifier, @Valid @RequestBody AdrCreateRequest request,
			@AuthenticationPrincipal Jwt jwt) {
		return service.create(adlIdentifier, request, actor(jwt));
	}

	@GetMapping("/adls/{adlIdentifier}/adrs")
	public List<AdrResponse> list(@PathVariable String adlIdentifier) {
		return service.list(adlIdentifier);
	}

	@GetMapping("/adrs/{identifier}")
	public AdrResponse detail(@PathVariable String identifier) {
		return service.detail(identifier);
	}

	@PutMapping("/adrs/{identifier}")
	public AdrResponse update(@PathVariable String identifier, @Valid @RequestBody AdrUpdateRequest request,
			@AuthenticationPrincipal Jwt jwt) {
		return service.update(identifier, request, actor(jwt));
	}

	@io.swagger.v3.oas.annotations.Operation(summary = "Submit an ADR for review",
			description = "Transitions a complete DRAFT ADR using its current version.")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409",
			description = "Stale version or invalid state transition")
	@PostMapping("/adrs/{identifier}/submit")
	public AdrResponse submit(@PathVariable String identifier, @Valid @RequestBody AdrActionRequest request,
			@AuthenticationPrincipal Jwt jwt) {
		return service.submit(identifier, request, actor(jwt));
	}

	@io.swagger.v3.oas.annotations.Operation(summary = "Cancel ADR review",
			description = "Returns an UNDER_REVIEW ADR to DRAFT using its current version.")
	@PostMapping("/adrs/{identifier}/cancel-review")
	public AdrResponse cancelReview(@PathVariable String identifier, @Valid @RequestBody AdrActionRequest request,
			@AuthenticationPrincipal Jwt jwt) {
		return service.cancelReview(identifier, request, actor(jwt));
	}

	@io.swagger.v3.oas.annotations.Operation(summary = "Approve an ADR",
			description = "An authenticated USER records a decision against the current version.")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409",
			description = "Stale version or ADR is not under review")
	@PostMapping("/adrs/{identifier}/approve")
	public AdrResponse approve(@PathVariable String identifier, @Valid @RequestBody AdrDecisionRequest request,
			@AuthenticationPrincipal Jwt jwt) {
		return service.approve(identifier, request, actor(jwt));
	}

	@io.swagger.v3.oas.annotations.Operation(summary = "Reject an ADR",
			description = "An authenticated USER supplies a required rejection justification with the current version.")
	@PostMapping("/adrs/{identifier}/reject")
	public AdrResponse reject(@PathVariable String identifier, @Valid @RequestBody AdrDecisionRequest request,
			@AuthenticationPrincipal Jwt jwt) {
		return service.reject(identifier, request, actor(jwt));
	}

	private AuditActor actor(Jwt jwt) {
		String name = jwt.getClaimAsString("name");
		return new AuditActor(jwt.getSubject(), name == null || name.isBlank() ? jwt.getSubject() : name);
	}

}
