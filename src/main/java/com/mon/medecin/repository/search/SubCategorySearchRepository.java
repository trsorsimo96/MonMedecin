package com.mon.medecin.repository.search;

import com.mon.medecin.domain.SubCategory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the SubCategory entity.
 */
public interface SubCategorySearchRepository extends ElasticsearchRepository<SubCategory, Long> {
}
