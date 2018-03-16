package com.mon.medecin.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mon.medecin.domain.Quarter;

import com.mon.medecin.repository.QuarterRepository;
import com.mon.medecin.repository.search.QuarterSearchRepository;
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
 * REST controller for managing Quarter.
 */
@RestController
@RequestMapping("/api")
public class QuarterResource {

    private final Logger log = LoggerFactory.getLogger(QuarterResource.class);

    private static final String ENTITY_NAME = "quarter";

    private final QuarterRepository quarterRepository;

    private final QuarterSearchRepository quarterSearchRepository;

    public QuarterResource(QuarterRepository quarterRepository, QuarterSearchRepository quarterSearchRepository) {
        this.quarterRepository = quarterRepository;
        this.quarterSearchRepository = quarterSearchRepository;
    }

    /**
     * POST  /quarters : Create a new quarter.
     *
     * @param quarter the quarter to create
     * @return the ResponseEntity with status 201 (Created) and with body the new quarter, or with status 400 (Bad Request) if the quarter has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/quarters")
    @Timed
    public ResponseEntity<Quarter> createQuarter(@Valid @RequestBody Quarter quarter) throws URISyntaxException {
        log.debug("REST request to save Quarter : {}", quarter);
        if (quarter.getId() != null) {
            throw new BadRequestAlertException("A new quarter cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Quarter result = quarterRepository.save(quarter);
        quarterSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/quarters/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /quarters : Updates an existing quarter.
     *
     * @param quarter the quarter to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated quarter,
     * or with status 400 (Bad Request) if the quarter is not valid,
     * or with status 500 (Internal Server Error) if the quarter couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/quarters")
    @Timed
    public ResponseEntity<Quarter> updateQuarter(@Valid @RequestBody Quarter quarter) throws URISyntaxException {
        log.debug("REST request to update Quarter : {}", quarter);
        if (quarter.getId() == null) {
            return createQuarter(quarter);
        }
        Quarter result = quarterRepository.save(quarter);
        quarterSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, quarter.getId().toString()))
            .body(result);
    }

    /**
     * GET  /quarters : get all the quarters.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of quarters in body
     */
    @GetMapping("/quarters")
    @Timed
    public List<Quarter> getAllQuarters() {
        log.debug("REST request to get all Quarters");
        return quarterRepository.findAll();
        }

    /**
     * GET  /quarters/:id : get the "id" quarter.
     *
     * @param id the id of the quarter to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the quarter, or with status 404 (Not Found)
     */
    @GetMapping("/quarters/{id}")
    @Timed
    public ResponseEntity<Quarter> getQuarter(@PathVariable Long id) {
        log.debug("REST request to get Quarter : {}", id);
        Quarter quarter = quarterRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(quarter));
    }

    /**
     * DELETE  /quarters/:id : delete the "id" quarter.
     *
     * @param id the id of the quarter to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/quarters/{id}")
    @Timed
    public ResponseEntity<Void> deleteQuarter(@PathVariable Long id) {
        log.debug("REST request to delete Quarter : {}", id);
        quarterRepository.delete(id);
        quarterSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/quarters?query=:query : search for the quarter corresponding
     * to the query.
     *
     * @param query the query of the quarter search
     * @return the result of the search
     */
    @GetMapping("/_search/quarters")
    @Timed
    public List<Quarter> searchQuarters(@RequestParam String query) {
        log.debug("REST request to search Quarters for query {}", query);
        return StreamSupport
            .stream(quarterSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
