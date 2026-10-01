package com.example.adrmanager.identity;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
class KeycloakActiveUserLookup implements ActiveUserLookup {

	private final KeycloakAdminGateway gateway;

	KeycloakActiveUserLookup(KeycloakAdminGateway gateway) {
		this.gateway = gateway;
	}

	@Override
	public Optional<KeycloakUser> findActiveUser(String userId) {
		return gateway.findById(userId).filter(KeycloakUser::enabled);
	}

}
