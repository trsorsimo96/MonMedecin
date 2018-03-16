package com.mon.medecin.repository.search;

import com.mon.medecin.domain.Service;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Service entity.
 */
public interface ServiceSearchRepository extends ElasticsearchRepository<Service, Long> {
}
