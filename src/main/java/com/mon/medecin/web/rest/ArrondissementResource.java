package com.mon.medecin.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mon.medecin.domain.Arrondissement;

import com.mon.medecin.repository.ArrondissementRepository;
import com.mon.medecin.repository.search.ArrondissementSearchRepository;
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
 * REST controller for managing Arrondissement.
 */
@RestController
@RequestMapping("/api")
public class ArrondissementResource {

    private final Logger log = LoggerFactory.getLogger(ArrondissementResource.class);

    private static final String ENTITY_NAME = "arrondissement";

    private final ArrondissementRepository arrondissementRepository;

    private final ArrondissementSearchRepository arrondissementSearchRepository;

    public ArrondissementResource(ArrondissementRepository arrondissementRepository, ArrondissementSearchRepository arrondissementSearchRepository) {
        this.arrondissementRepository = arrondissementRepository;
        this.arrondissementSearchRepository = arrondissementSearchRepository;
    }

    /**
     * POST  /arrondissements : Create a new arrondissement.
     *
     * @param arrondissement the arrondissement to create
     * @return the ResponseEntity with status 201 (Created) and with body the new arrondissement, or with status 400 (Bad Request) if the arrondissement has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/arrondissements")
    @Timed
    public ResponseEntity<Arrondissement> createArrondissement(@Valid @RequestBody Arrondissement arrondissement) throws URISyntaxException {
        log.debug("REST request to save Arrondissement : {}", arrondissement);
        if (arrondissement.getId() != null) {
            throw new BadRequestAlertException("A new arrondissement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Arrondissement result = arrondissementRepository.save(arrondissement);
        arrondissementSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/arrondissements/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /arrondissements : Updates an existing arrondissement.
     *
     * @param arrondissement the arrondissement to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated arrondissement,
     * or with status 400 (Bad Request) if the arrondissement is not valid,
     * or with status 500 (Internal Server Error) if the arrondissement couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/arrondissements")
    @Timed
    public ResponseEntity<Arrondissement> updateArrondissement(@Valid @RequestBody Arrondissement arrondissement) throws URISyntaxException {
        log.debug("REST request to update Arrondissement : {}", arrondissement);
        if (arrondissement.getId() == null) {
            return createArrondissement(arrondissement);
        }
        Arrondissement result = arrondissementRepository.save(arrondissement);
        arrondissementSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, arrondissement.getId().toString()))
            .body(result);
    }

    /**
     * GET  /arrondissements : get all the arrondissements.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of arrondissements in body
     */
    @GetMapping("/arrondissements")
    @Timed
    public List<Arrondissement> getAllArrondissements() {
        log.debug("REST request to get all Arrondissements");
        return arrondissementRepository.findAll();
        }

    /**
     * GET  /arrondissements/:id : get the "id" arrondissement.
     *
     * @param id the id of the arrondissement to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the arrondissement, or with status 404 (Not Found)
     */
    @GetMapping("/arrondissements/{id}")
    @Timed
    public ResponseEntity<Arrondissement> getArrondissement(@PathVariable Long id) {
        log.debug("REST request to get Arrondissement : {}", id);
        Arrondissement arrondissement = arrondissementRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(arrondissement));
    }

    /**
     * DELETE  /arrondissements/:id : delete the "id" arrondissement.
     *
     * @param id the id of the arrondissement to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/arrondissements/{id}")
    @Timed
    public ResponseEntity<Void> deleteArrondissement(@PathVariable Long id) {
        log.debug("REST request to delete Arrondissement : {}", id);
        arrondissementRepository.delete(id);
        arrondissementSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/arrondissements?query=:query : search for the arrondissement corresponding
     * to the query.
     *
     * @param query the query of the arrondissement search
     * @return the result of the search
     */
    @GetMapping("/_search/arrondissements")
    @Timed
    public List<Arrondissement> searchArrondissements(@RequestParam String query) {
        log.debug("REST request to search Arrondissements for query {}", query);
        return StreamSupport
            .stream(arrondissementSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
