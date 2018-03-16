package com.mon.medecin.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mon.medecin.domain.Region;

import com.mon.medecin.repository.RegionRepository;
import com.mon.medecin.repository.search.RegionSearchRepository;
import com.mon.medecin.web.rest.errors.BadRequestAlertException;
import com.mon.medecin.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Region.
 */
@RestController
@RequestMapping("/api")
public class RegionResource {

    private final Logger log = LoggerFactory.getLogger(RegionResource.class);

    private static final String ENTITY_NAME = "region";

    private final RegionRepository regionRepository;

    private final RegionSearchRepository regionSearchRepository;

    public RegionResource(RegionRepository regionRepository, RegionSearchRepository regionSearchRepository) {
        this.regionRepository = regionRepository;
        this.regionSearchRepository = regionSearchRepository;
    }

    /**
     * POST  /regions : Create a new region.
     *
     * @param region the region to create
     * @return the ResponseEntity with status 201 (Created) and with body the new region, or with status 400 (Bad Request) if the region has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/regions")
    @Timed
    public ResponseEntity<Region> createRegion(@Valid @RequestBody Region region) throws URISyntaxException {
        log.debug("REST request to save Region : {}", region);
        if (region.getId() != null) {
            throw new BadRequestAlertException("A new region cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Region result = regionRepository.save(region);
        regionSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/regions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /regions : Updates an existing region.
     *
     * @param region the region to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated region,
     * or with status 400 (Bad Request) if the region is not valid,
     * or with status 500 (Internal Server Error) if the region couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/regions")
    @Timed
    public ResponseEntity<Region> updateRegion(@Valid @RequestBody Region region) throws URISyntaxException {
        log.debug("REST request to update Region : {}", region);
        if (region.getId() == null) {
            return createRegion(region);
        }
        Region result = regionRepository.save(region);
        regionSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, region.getId().toString()))
            .body(result);
    }

    /**
     * GET  /regions : get all the regions.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of regions in body
     */
    @GetMapping("/regions")
    @Timed
    public List<Region> getAllRegions() {
        log.debug("REST request to get all Regions");
        return regionRepository.findAll();
        }

    /**
     * GET  /regions/:id : get the "id" region.
     *
     * @param id the id of the region to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the region, or with status 404 (Not Found)
     */
    @GetMapping("/regions/{id}")
    @Timed
    public ResponseEntity<Region> getRegion(@PathVariable Long id) {
        log.debug("REST request to get Region : {}", id);
        Region region = regionRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(region));
    }

    /**
     * DELETE  /regions/:id : delete the "id" region.
     *
     * @param id the id of the region to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/regions/{id}")
    @Timed
    public ResponseEntity<Void> deleteRegion(@PathVariable Long id) {
        log.debug("REST request to delete Region : {}", id);
        regionRepository.delete(id);
        regionSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/regions?query=:query : search for the region corresponding
     * to the query.
     *
     * @param query the query of the region search
     * @return the result of the search
     */
    @GetMapping("/_search/regions")
    @Timed
    public List<Region> searchRegions(@RequestParam String query) {
        log.debug("REST request to search Regions for query {}", query);
        return StreamSupport
            .stream(regionSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
