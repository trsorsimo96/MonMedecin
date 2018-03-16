package com.mon.medecin.repository;

import com.mon.medecin.domain.Hospital;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Hospital entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

}
