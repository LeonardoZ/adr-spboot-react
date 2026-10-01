package com.example.adrmanager.adl;

import java.time.Instant;

public record AdlSearchCriteria(String identifier, String text, String tag, Instant createdFrom, Instant createdTo,
		Boolean archived) {
}
