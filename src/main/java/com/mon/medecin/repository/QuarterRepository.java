package com.mon.medecin.repository;

import com.mon.medecin.domain.Quarter;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Quarter entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuarterRepository extends JpaRepository<Quarter, Long> {

}
