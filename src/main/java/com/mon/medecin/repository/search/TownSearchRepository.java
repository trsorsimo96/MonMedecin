package com.mon.medecin.repository.search;

import com.mon.medecin.domain.Town;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Town entity.
 */
public interface TownSearchRepository extends ElasticsearchRepository<Town, Long> {
}
