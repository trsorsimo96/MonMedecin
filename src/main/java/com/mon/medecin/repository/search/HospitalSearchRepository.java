package com.mon.medecin.repository.search;

import com.mon.medecin.domain.Hospital;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Hospital entity.
 */
public interface HospitalSearchRepository extends ElasticsearchRepository<Hospital, Long> {
}
