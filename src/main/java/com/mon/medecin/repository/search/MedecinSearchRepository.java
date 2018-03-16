package com.mon.medecin.repository.search;

import com.mon.medecin.domain.Medecin;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Medecin entity.
 */
public interface MedecinSearchRepository extends ElasticsearchRepository<Medecin, Long> {
}
