package com.mon.medecin.repository.search;

import com.mon.medecin.domain.Arrondissement;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Arrondissement entity.
 */
public interface ArrondissementSearchRepository extends ElasticsearchRepository<Arrondissement, Long> {
}
