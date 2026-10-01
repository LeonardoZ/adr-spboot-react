package com.example.adrmanager.security;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class SecurePingController {

	@GetMapping("/ping")
	Map<String, String> ping() {
		return Map.of("status", "ok");
	}

}
