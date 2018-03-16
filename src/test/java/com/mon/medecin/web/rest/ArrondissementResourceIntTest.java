package com.mon.medecin.web.rest;

import com.mon.medecin.MonMedecinApp;

import com.mon.medecin.domain.Arrondissement;
import com.mon.medecin.repository.ArrondissementRepository;
import com.mon.medecin.repository.search.ArrondissementSearchRepository;
import com.mon.medecin.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.mon.medecin.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ArrondissementResource REST controller.
 *
 * @see ArrondissementResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MonMedecinApp.class)
public class ArrondissementResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private ArrondissementRepository arrondissementRepository;

    @Autowired
    private ArrondissementSearchRepository arrondissementSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restArrondissementMockMvc;

    private Arrondissement arrondissement;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ArrondissementResource arrondissementResource = new ArrondissementResource(arrondissementRepository, arrondissementSearchRepository);
        this.restArrondissementMockMvc = MockMvcBuilders.standaloneSetup(arrondissementResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Arrondissement createEntity(EntityManager em) {
        Arrondissement arrondissement = new Arrondissement()
            .name(DEFAULT_NAME);
        return arrondissement;
    }

    @Before
    public void initTest() {
        arrondissementSearchRepository.deleteAll();
        arrondissement = createEntity(em);
    }

    @Test
    @Transactional
    public void createArrondissement() throws Exception {
        int databaseSizeBeforeCreate = arrondissementRepository.findAll().size();

        // Create the Arrondissement
        restArrondissementMockMvc.perform(post("/api/arrondissements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(arrondissement)))
            .andExpect(status().isCreated());

        // Validate the Arrondissement in the database
        List<Arrondissement> arrondissementList = arrondissementRepository.findAll();
        assertThat(arrondissementList).hasSize(databaseSizeBeforeCreate + 1);
        Arrondissement testArrondissement = arrondissementList.get(arrondissementList.size() - 1);
        assertThat(testArrondissement.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Arrondissement in Elasticsearch
        Arrondissement arrondissementEs = arrondissementSearchRepository.findOne(testArrondissement.getId());
        assertThat(arrondissementEs).isEqualToIgnoringGivenFields(testArrondissement);
    }

    @Test
    @Transactional
    public void createArrondissementWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = arrondissementRepository.findAll().size();

        // Create the Arrondissement with an existing ID
        arrondissement.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restArrondissementMockMvc.perform(post("/api/arrondissements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(arrondissement)))
            .andExpect(status().isBadRequest());

        // Validate the Arrondissement in the database
        List<Arrondissement> arrondissementList = arrondissementRepository.findAll();
        assertThat(arrondissementList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = arrondissementRepository.findAll().size();
        // set the field null
        arrondissement.setName(null);

        // Create the Arrondissement, which fails.

        restArrondissementMockMvc.perform(post("/api/arrondissements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(arrondissement)))
            .andExpect(status().isBadRequest());

        List<Arrondissement> arrondissementList = arrondissementRepository.findAll();
        assertThat(arrondissementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllArrondissements() throws Exception {
        // Initialize the database
        arrondissementRepository.saveAndFlush(arrondissement);

        // Get all the arrondissementList
        restArrondissementMockMvc.perform(get("/api/arrondissements?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(arrondissement.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getArrondissement() throws Exception {
        // Initialize the database
        arrondissementRepository.saveAndFlush(arrondissement);

        // Get the arrondissement
        restArrondissementMockMvc.perform(get("/api/arrondissements/{id}", arrondissement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(arrondissement.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingArrondissement() throws Exception {
        // Get the arrondissement
        restArrondissementMockMvc.perform(get("/api/arrondissements/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateArrondissement() throws Exception {
        // Initialize the database
        arrondissementRepository.saveAndFlush(arrondissement);
        arrondissementSearchRepository.save(arrondissement);
        int databaseSizeBeforeUpdate = arrondissementRepository.findAll().size();

        // Update the arrondissement
        Arrondissement updatedArrondissement = arrondissementRepository.findOne(arrondissement.getId());
        // Disconnect from session so that the updates on updatedArrondissement are not directly saved in db
        em.detach(updatedArrondissement);
        updatedArrondissement
            .name(UPDATED_NAME);

        restArrondissementMockMvc.perform(put("/api/arrondissements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedArrondissement)))
            .andExpect(status().isOk());

        // Validate the Arrondissement in the database
        List<Arrondissement> arrondissementList = arrondissementRepository.findAll();
        assertThat(arrondissementList).hasSize(databaseSizeBeforeUpdate);
        Arrondissement testArrondissement = arrondissementList.get(arrondissementList.size() - 1);
        assertThat(testArrondissement.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Arrondissement in Elasticsearch
        Arrondissement arrondissementEs = arrondissementSearchRepository.findOne(testArrondissement.getId());
        assertThat(arrondissementEs).isEqualToIgnoringGivenFields(testArrondissement);
    }

    @Test
    @Transactional
    public void updateNonExistingArrondissement() throws Exception {
        int databaseSizeBeforeUpdate = arrondissementRepository.findAll().size();

        // Create the Arrondissement

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restArrondissementMockMvc.perform(put("/api/arrondissements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(arrondissement)))
            .andExpect(status().isCreated());

        // Validate the Arrondissement in the database
        List<Arrondissement> arrondissementList = arrondissementRepository.findAll();
        assertThat(arrondissementList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteArrondissement() throws Exception {
        // Initialize the database
        arrondissementRepository.saveAndFlush(arrondissement);
        arrondissementSearchRepository.save(arrondissement);
        int databaseSizeBeforeDelete = arrondissementRepository.findAll().size();

        // Get the arrondissement
        restArrondissementMockMvc.perform(delete("/api/arrondissements/{id}", arrondissement.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean arrondissementExistsInEs = arrondissementSearchRepository.exists(arrondissement.getId());
        assertThat(arrondissementExistsInEs).isFalse();

        // Validate the database is empty
        List<Arrondissement> arrondissementList = arrondissementRepository.findAll();
        assertThat(arrondissementList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchArrondissement() throws Exception {
        // Initialize the database
        arrondissementRepository.saveAndFlush(arrondissement);
        arrondissementSearchRepository.save(arrondissement);

        // Search the arrondissement
        restArrondissementMockMvc.perform(get("/api/_search/arrondissements?query=id:" + arrondissement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(arrondissement.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Arrondissement.class);
        Arrondissement arrondissement1 = new Arrondissement();
        arrondissement1.setId(1L);
        Arrondissement arrondissement2 = new Arrondissement();
        arrondissement2.setId(arrondissement1.getId());
        assertThat(arrondissement1).isEqualTo(arrondissement2);
        arrondissement2.setId(2L);
        assertThat(arrondissement1).isNotEqualTo(arrondissement2);
        arrondissement1.setId(null);
        assertThat(arrondissement1).isNotEqualTo(arrondissement2);
    }
}
