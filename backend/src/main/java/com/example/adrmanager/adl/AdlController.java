package com.example.adrmanager.adl;

import com.example.adrmanager.audit.AuditActor;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/adls")
@PreAuthorize("hasRole('USER')")
public class AdlController {

	private final AdlService service;

	public AdlController(AdlService service) {
		this.service = service;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public AdlResponse create(@Valid @RequestBody AdlCreateRequest request, @AuthenticationPrincipal Jwt jwt) {
		String name = jwt.getClaimAsString("name");
		return service.create(request, new AuditActor(jwt.getSubject(), name == null ? jwt.getSubject() : name));
	}

	@io.swagger.v3.oas.annotations.Operation(summary = "Search ADLs",
			description = "Returns an authenticated company-wide paginated ADL register. Filters combine with AND; archived ADLs are hidden unless requested.")
	@io.swagger.v3.oas.annotations.responses.ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "ADL page"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
					description = "Invalid pagination, filter, or sort parameter"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
					description = "Authentication required") })
	@GetMapping
	public AdlPageResponse search(
			@io.swagger.v3.oas.annotations.Parameter(description = "Immutable ADL identifier filter",
					example = "ADL-0001") @RequestParam(required = false) String identifier,
			@io.swagger.v3.oas.annotations.Parameter(description = "Text search across title, context, and problem",
					example = "payment") @RequestParam(required = false) String text,
			@RequestParam(required = false) String tag, @RequestParam(required = false) Instant createdFrom,
			@RequestParam(required = false) Instant createdTo,
			@io.swagger.v3.oas.annotations.Parameter(description = "Include archived ADLs; false by default",
					example = "true") @RequestParam(required = false) Boolean archived,
			@io.swagger.v3.oas.annotations.Parameter(description = "Zero-based page",
					example = "0") @RequestParam(defaultValue = "0") int page,
			@io.swagger.v3.oas.annotations.Parameter(description = "Page size, from 1 to 100",
					example = "20") @RequestParam(defaultValue = "20") int size,
			@io.swagger.v3.oas.annotations.Parameter(description = "Allowed sort field and direction",
					example = "createdAt,desc") @RequestParam(required = false) String sort) {
		return service.search(new AdlSearchCriteria(identifier, text, tag, createdFrom, createdTo, archived), page,
				size, sort);
	}

	@GetMapping("/{identifier}")
	public AdlResponse detail(@PathVariable String identifier) {
		return service.detail(identifier);
	}

	@io.swagger.v3.oas.annotations.Operation(summary = "Update an ADL",
			description = "Requires the current version in the request body; stale changes return 409.")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Optimistic-lock conflict")
	@PutMapping("/{identifier}")
	public AdlResponse update(@PathVariable String identifier, @Valid @RequestBody AdlUpdateRequest request,
			@AuthenticationPrincipal Jwt jwt) {
		String name = jwt.getClaimAsString("name");
		return service.update(identifier, request,
				new AuditActor(jwt.getSubject(), name == null ? jwt.getSubject() : name));
	}

	@io.swagger.v3.oas.annotations.Operation(summary = "Archive an ADL",
			description = "Archives an ADL using its current version. Archival is rejected while an ADR is under review.")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409",
			description = "Stale version or an ADR is under review")
	@PostMapping("/{identifier}/archive")
	public AdlResponse archive(@PathVariable String identifier, @Valid @RequestBody AdlArchiveRequest request,
			@AuthenticationPrincipal Jwt jwt) {
		String name = jwt.getClaimAsString("name");
		return service.archive(identifier, request,
				new AuditActor(jwt.getSubject(), name == null ? jwt.getSubject() : name));
	}

}
