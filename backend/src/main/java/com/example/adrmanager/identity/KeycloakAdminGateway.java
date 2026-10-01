package com.example.adrmanager.identity;

import java.util.Optional;

interface KeycloakAdminGateway {

	Optional<KeycloakUser> findById(String userId);

}
