package com.mon.medecin.repository;

import com.mon.medecin.domain.Arrondissement;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Arrondissement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ArrondissementRepository extends JpaRepository<Arrondissement, Long> {

}
