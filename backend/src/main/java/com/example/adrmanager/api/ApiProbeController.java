package com.example.adrmanager.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/probes")
class ApiProbeController {

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	ProbeRequest validate(@Valid @RequestBody ProbeRequest request) {
		return request;
	}

	@GetMapping("/missing/{id}")
	void missing(@PathVariable String id) {
		throw new ResourceNotFoundException("Resource " + id + " was not found");
	}

	@PostMapping("/conflict")
	void conflict() {
		throw new ConflictException("The resource was changed by another request");
	}

	record ProbeRequest(@NotBlank(message = "name is required") String name) {
	}

}
