package com.example.adrmanager.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class KeycloakActiveUserLookupTest {

	@Test
	void returnsActiveUserWithDisplayNameSnapshot() {
		KeycloakActiveUserLookup lookup = new KeycloakActiveUserLookup(
				userId -> Optional.of(new KeycloakUser(userId, "alice", "Alice Architect", true)));

		KeycloakUser user = lookup.findActiveUser("user-1").orElseThrow();

		assertEquals("Alice Architect", user.displayName());
	}

	@Test
	void excludesInactiveUser() {
		KeycloakActiveUserLookup lookup = new KeycloakActiveUserLookup(
				userId -> Optional.of(new KeycloakUser(userId, "inactive", "Inactive User", false)));

		assertEquals(Optional.empty(), lookup.findActiveUser("user-2"));
	}

	@Test
	void returnsEmptyForUnknownUser() {
		KeycloakActiveUserLookup lookup = new KeycloakActiveUserLookup(userId -> Optional.empty());

		assertEquals(Optional.empty(), lookup.findActiveUser("missing"));
	}

	@Test
	void propagatesUnavailableKeycloak() {
		KeycloakActiveUserLookup lookup = new KeycloakActiveUserLookup(userId -> {
			throw new KeycloakUnavailableException("unavailable", null);
		});

		assertThrows(KeycloakUnavailableException.class, () -> lookup.findActiveUser("user-3"));
	}

}
