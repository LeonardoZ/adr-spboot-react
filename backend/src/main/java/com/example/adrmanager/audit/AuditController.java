package com.example.adrmanager.audit;

import com.example.adrmanager.adl.AdlRepository;
import com.example.adrmanager.adr.AdrRepository;
import com.example.adrmanager.api.ResourceNotFoundException;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('USER')")
public class AuditController {

	private final AuditEventRepository events;

	private final AdlRepository adls;

	private final AdrRepository adrs;

	public AuditController(AuditEventRepository events, AdlRepository adls, AdrRepository adrs) {
		this.events = events;
		this.adls = adls;
		this.adrs = adrs;
	}

	@GetMapping("/adls/{identifier}/audit-events")
	public List<AuditEventResponse> adlHistory(@PathVariable String identifier) {
		if (adls.findByBusinessIdentifier(identifier).isEmpty())
			throw new ResourceNotFoundException("ADL " + identifier + " was not found");
		return events.findByEntityTypeAndEntityIdentifierOrderByOccurredAtAsc(AuditEntityType.ADL, identifier)
			.stream()
			.map(AuditEventResponse::from)
			.toList();
	}

	@GetMapping("/adrs/{identifier}/audit-events")
	public List<AuditEventResponse> adrHistory(@PathVariable String identifier) {
		if (adrs.findByBusinessIdentifier(identifier).isEmpty())
			throw new ResourceNotFoundException("ADR " + identifier + " was not found");
		return events.findByEntityTypeAndEntityIdentifierOrderByOccurredAtAsc(AuditEntityType.ADR, identifier)
			.stream()
			.map(AuditEventResponse::from)
			.toList();
	}

}
