package com.mon.medecin.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mon.medecin.domain.Medecin;

import com.mon.medecin.repository.MedecinRepository;
import com.mon.medecin.repository.search.MedecinSearchRepository;
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
 * REST controller for managing Medecin.
 */
@RestController
@RequestMapping("/api")
public class MedecinResource {

    private final Logger log = LoggerFactory.getLogger(MedecinResource.class);

    private static final String ENTITY_NAME = "medecin";

    private final MedecinRepository medecinRepository;

    private final MedecinSearchRepository medecinSearchRepository;

    public MedecinResource(MedecinRepository medecinRepository, MedecinSearchRepository medecinSearchRepository) {
        this.medecinRepository = medecinRepository;
        this.medecinSearchRepository = medecinSearchRepository;
    }

    /**
     * POST  /medecins : Create a new medecin.
     *
     * @param medecin the medecin to create
     * @return the ResponseEntity with status 201 (Created) and with body the new medecin, or with status 400 (Bad Request) if the medecin has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/medecins")
    @Timed
    public ResponseEntity<Medecin> createMedecin(@Valid @RequestBody Medecin medecin) throws URISyntaxException {
        log.debug("REST request to save Medecin : {}", medecin);
        if (medecin.getId() != null) {
            throw new BadRequestAlertException("A new medecin cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Medecin result = medecinRepository.save(medecin);
        medecinSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/medecins/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /medecins : Updates an existing medecin.
     *
     * @param medecin the medecin to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated medecin,
     * or with status 400 (Bad Request) if the medecin is not valid,
     * or with status 500 (Internal Server Error) if the medecin couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/medecins")
    @Timed
    public ResponseEntity<Medecin> updateMedecin(@Valid @RequestBody Medecin medecin) throws URISyntaxException {
        log.debug("REST request to update Medecin : {}", medecin);
        if (medecin.getId() == null) {
            return createMedecin(medecin);
        }
        Medecin result = medecinRepository.save(medecin);
        medecinSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, medecin.getId().toString()))
            .body(result);
    }

    /**
     * GET  /medecins : get all the medecins.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of medecins in body
     */
    @GetMapping("/medecins")
    @Timed
    public List<Medecin> getAllMedecins() {
        log.debug("REST request to get all Medecins");
        return medecinRepository.findAll();
        }

    /**
     * GET  /medecins/:id : get the "id" medecin.
     *
     * @param id the id of the medecin to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the medecin, or with status 404 (Not Found)
     */
    @GetMapping("/medecins/{id}")
    @Timed
    public ResponseEntity<Medecin> getMedecin(@PathVariable Long id) {
        log.debug("REST request to get Medecin : {}", id);
        Medecin medecin = medecinRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(medecin));
    }

    /**
     * DELETE  /medecins/:id : delete the "id" medecin.
     *
     * @param id the id of the medecin to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/medecins/{id}")
    @Timed
    public ResponseEntity<Void> deleteMedecin(@PathVariable Long id) {
        log.debug("REST request to delete Medecin : {}", id);
        medecinRepository.delete(id);
        medecinSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/medecins?query=:query : search for the medecin corresponding
     * to the query.
     *
     * @param query the query of the medecin search
     * @return the result of the search
     */
    @GetMapping("/_search/medecins")
    @Timed
    public List<Medecin> searchMedecins(@RequestParam String query) {
        log.debug("REST request to search Medecins for query {}", query);
        return StreamSupport
            .stream(medecinSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
