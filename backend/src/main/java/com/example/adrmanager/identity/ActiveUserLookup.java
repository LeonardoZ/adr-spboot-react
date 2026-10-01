package com.example.adrmanager.identity;

import java.util.Optional;

public interface ActiveUserLookup {

	Optional<KeycloakUser> findActiveUser(String userId);

}
