package com.example.adrmanager.adl;

import java.util.List;

public record AdlPageResponse(List<AdlResponse> content, int page, int size, long totalElements, int totalPages) {
}
