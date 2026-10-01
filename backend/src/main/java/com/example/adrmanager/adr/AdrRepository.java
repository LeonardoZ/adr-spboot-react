package com.example.adrmanager.adr;

import java.util.Optional;
import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdrRepository extends JpaRepository<Adr, Long> {

	Optional<Adr> findByBusinessIdentifier(String businessIdentifier);

	boolean existsByAdlBusinessIdentifierAndStatus(String adlIdentifier, AdrStatus status);

	List<Adr> findByAdlBusinessIdentifierOrderByCreatedAtAsc(String adlIdentifier);

	@Query("select new com.example.adrmanager.adr.AdrSummaryResponse(adr.businessIdentifier, adr.title, adr.status, adr.authorDisplayName, adr.createdAt) "
			+ "from Adr adr where adr.adl.businessIdentifier = :adlIdentifier order by adr.createdAt asc")
	List<AdrSummaryResponse> findSummariesByAdlIdentifier(@Param("adlIdentifier") String adlIdentifier);

}
