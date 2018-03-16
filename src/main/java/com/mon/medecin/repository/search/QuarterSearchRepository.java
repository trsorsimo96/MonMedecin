package com.mon.medecin.repository.search;

import com.mon.medecin.domain.Quarter;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Quarter entity.
 */
public interface QuarterSearchRepository extends ElasticsearchRepository<Quarter, Long> {
}
