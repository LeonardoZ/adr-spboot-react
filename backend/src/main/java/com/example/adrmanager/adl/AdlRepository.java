package com.example.adrmanager.adl;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdlRepository extends JpaRepository<Adl, Long>, JpaSpecificationExecutor<Adl> {

	Optional<Adl> findByBusinessIdentifier(String businessIdentifier);

}
