package com.mon.medecin.web.rest;

import com.mon.medecin.MonMedecinApp;

import com.mon.medecin.domain.Medecin;
import com.mon.medecin.repository.MedecinRepository;
import com.mon.medecin.repository.search.MedecinSearchRepository;
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
 * Test class for the MedecinResource REST controller.
 *
 * @see MedecinResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MonMedecinApp.class)
public class MedecinResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    @Autowired
    private MedecinRepository medecinRepository;

    @Autowired
    private MedecinSearchRepository medecinSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMedecinMockMvc;

    private Medecin medecin;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MedecinResource medecinResource = new MedecinResource(medecinRepository, medecinSearchRepository);
        this.restMedecinMockMvc = MockMvcBuilders.standaloneSetup(medecinResource)
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
    public static Medecin createEntity(EntityManager em) {
        Medecin medecin = new Medecin()
            .name(DEFAULT_NAME)
            .title(DEFAULT_TITLE)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE);
        return medecin;
    }

    @Before
    public void initTest() {
        medecinSearchRepository.deleteAll();
        medecin = createEntity(em);
    }

    @Test
    @Transactional
    public void createMedecin() throws Exception {
        int databaseSizeBeforeCreate = medecinRepository.findAll().size();

        // Create the Medecin
        restMedecinMockMvc.perform(post("/api/medecins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(medecin)))
            .andExpect(status().isCreated());

        // Validate the Medecin in the database
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeCreate + 1);
        Medecin testMedecin = medecinList.get(medecinList.size() - 1);
        assertThat(testMedecin.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMedecin.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testMedecin.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testMedecin.getPhone()).isEqualTo(DEFAULT_PHONE);

        // Validate the Medecin in Elasticsearch
        Medecin medecinEs = medecinSearchRepository.findOne(testMedecin.getId());
        assertThat(medecinEs).isEqualToIgnoringGivenFields(testMedecin);
    }

    @Test
    @Transactional
    public void createMedecinWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = medecinRepository.findAll().size();

        // Create the Medecin with an existing ID
        medecin.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMedecinMockMvc.perform(post("/api/medecins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(medecin)))
            .andExpect(status().isBadRequest());

        // Validate the Medecin in the database
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = medecinRepository.findAll().size();
        // set the field null
        medecin.setName(null);

        // Create the Medecin, which fails.

        restMedecinMockMvc.perform(post("/api/medecins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(medecin)))
            .andExpect(status().isBadRequest());

        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = medecinRepository.findAll().size();
        // set the field null
        medecin.setTitle(null);

        // Create the Medecin, which fails.

        restMedecinMockMvc.perform(post("/api/medecins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(medecin)))
            .andExpect(status().isBadRequest());

        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMedecins() throws Exception {
        // Initialize the database
        medecinRepository.saveAndFlush(medecin);

        // Get all the medecinList
        restMedecinMockMvc.perform(get("/api/medecins?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(medecin.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE.toString())));
    }

    @Test
    @Transactional
    public void getMedecin() throws Exception {
        // Initialize the database
        medecinRepository.saveAndFlush(medecin);

        // Get the medecin
        restMedecinMockMvc.perform(get("/api/medecins/{id}", medecin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(medecin.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMedecin() throws Exception {
        // Get the medecin
        restMedecinMockMvc.perform(get("/api/medecins/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMedecin() throws Exception {
        // Initialize the database
        medecinRepository.saveAndFlush(medecin);
        medecinSearchRepository.save(medecin);
        int databaseSizeBeforeUpdate = medecinRepository.findAll().size();

        // Update the medecin
        Medecin updatedMedecin = medecinRepository.findOne(medecin.getId());
        // Disconnect from session so that the updates on updatedMedecin are not directly saved in db
        em.detach(updatedMedecin);
        updatedMedecin
            .name(UPDATED_NAME)
            .title(UPDATED_TITLE)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE);

        restMedecinMockMvc.perform(put("/api/medecins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMedecin)))
            .andExpect(status().isOk());

        // Validate the Medecin in the database
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeUpdate);
        Medecin testMedecin = medecinList.get(medecinList.size() - 1);
        assertThat(testMedecin.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMedecin.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testMedecin.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testMedecin.getPhone()).isEqualTo(UPDATED_PHONE);

        // Validate the Medecin in Elasticsearch
        Medecin medecinEs = medecinSearchRepository.findOne(testMedecin.getId());
        assertThat(medecinEs).isEqualToIgnoringGivenFields(testMedecin);
    }

    @Test
    @Transactional
    public void updateNonExistingMedecin() throws Exception {
        int databaseSizeBeforeUpdate = medecinRepository.findAll().size();

        // Create the Medecin

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMedecinMockMvc.perform(put("/api/medecins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(medecin)))
            .andExpect(status().isCreated());

        // Validate the Medecin in the database
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMedecin() throws Exception {
        // Initialize the database
        medecinRepository.saveAndFlush(medecin);
        medecinSearchRepository.save(medecin);
        int databaseSizeBeforeDelete = medecinRepository.findAll().size();

        // Get the medecin
        restMedecinMockMvc.perform(delete("/api/medecins/{id}", medecin.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean medecinExistsInEs = medecinSearchRepository.exists(medecin.getId());
        assertThat(medecinExistsInEs).isFalse();

        // Validate the database is empty
        List<Medecin> medecinList = medecinRepository.findAll();
        assertThat(medecinList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchMedecin() throws Exception {
        // Initialize the database
        medecinRepository.saveAndFlush(medecin);
        medecinSearchRepository.save(medecin);

        // Search the medecin
        restMedecinMockMvc.perform(get("/api/_search/medecins?query=id:" + medecin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(medecin.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Medecin.class);
        Medecin medecin1 = new Medecin();
        medecin1.setId(1L);
        Medecin medecin2 = new Medecin();
        medecin2.setId(medecin1.getId());
        assertThat(medecin1).isEqualTo(medecin2);
        medecin2.setId(2L);
        assertThat(medecin1).isNotEqualTo(medecin2);
        medecin1.setId(null);
        assertThat(medecin1).isNotEqualTo(medecin2);
    }
}
