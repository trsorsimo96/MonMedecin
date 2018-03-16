package com.mon.medecin.web.rest;

import com.mon.medecin.MonMedecinApp;

import com.mon.medecin.domain.Quarter;
import com.mon.medecin.repository.QuarterRepository;
import com.mon.medecin.repository.search.QuarterSearchRepository;
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
 * Test class for the QuarterResource REST controller.
 *
 * @see QuarterResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MonMedecinApp.class)
public class QuarterResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private QuarterRepository quarterRepository;

    @Autowired
    private QuarterSearchRepository quarterSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restQuarterMockMvc;

    private Quarter quarter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final QuarterResource quarterResource = new QuarterResource(quarterRepository, quarterSearchRepository);
        this.restQuarterMockMvc = MockMvcBuilders.standaloneSetup(quarterResource)
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
    public static Quarter createEntity(EntityManager em) {
        Quarter quarter = new Quarter()
            .name(DEFAULT_NAME);
        return quarter;
    }

    @Before
    public void initTest() {
        quarterSearchRepository.deleteAll();
        quarter = createEntity(em);
    }

    @Test
    @Transactional
    public void createQuarter() throws Exception {
        int databaseSizeBeforeCreate = quarterRepository.findAll().size();

        // Create the Quarter
        restQuarterMockMvc.perform(post("/api/quarters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quarter)))
            .andExpect(status().isCreated());

        // Validate the Quarter in the database
        List<Quarter> quarterList = quarterRepository.findAll();
        assertThat(quarterList).hasSize(databaseSizeBeforeCreate + 1);
        Quarter testQuarter = quarterList.get(quarterList.size() - 1);
        assertThat(testQuarter.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Quarter in Elasticsearch
        Quarter quarterEs = quarterSearchRepository.findOne(testQuarter.getId());
        assertThat(quarterEs).isEqualToIgnoringGivenFields(testQuarter);
    }

    @Test
    @Transactional
    public void createQuarterWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = quarterRepository.findAll().size();

        // Create the Quarter with an existing ID
        quarter.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuarterMockMvc.perform(post("/api/quarters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quarter)))
            .andExpect(status().isBadRequest());

        // Validate the Quarter in the database
        List<Quarter> quarterList = quarterRepository.findAll();
        assertThat(quarterList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = quarterRepository.findAll().size();
        // set the field null
        quarter.setName(null);

        // Create the Quarter, which fails.

        restQuarterMockMvc.perform(post("/api/quarters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quarter)))
            .andExpect(status().isBadRequest());

        List<Quarter> quarterList = quarterRepository.findAll();
        assertThat(quarterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllQuarters() throws Exception {
        // Initialize the database
        quarterRepository.saveAndFlush(quarter);

        // Get all the quarterList
        restQuarterMockMvc.perform(get("/api/quarters?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quarter.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getQuarter() throws Exception {
        // Initialize the database
        quarterRepository.saveAndFlush(quarter);

        // Get the quarter
        restQuarterMockMvc.perform(get("/api/quarters/{id}", quarter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(quarter.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingQuarter() throws Exception {
        // Get the quarter
        restQuarterMockMvc.perform(get("/api/quarters/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQuarter() throws Exception {
        // Initialize the database
        quarterRepository.saveAndFlush(quarter);
        quarterSearchRepository.save(quarter);
        int databaseSizeBeforeUpdate = quarterRepository.findAll().size();

        // Update the quarter
        Quarter updatedQuarter = quarterRepository.findOne(quarter.getId());
        // Disconnect from session so that the updates on updatedQuarter are not directly saved in db
        em.detach(updatedQuarter);
        updatedQuarter
            .name(UPDATED_NAME);

        restQuarterMockMvc.perform(put("/api/quarters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedQuarter)))
            .andExpect(status().isOk());

        // Validate the Quarter in the database
        List<Quarter> quarterList = quarterRepository.findAll();
        assertThat(quarterList).hasSize(databaseSizeBeforeUpdate);
        Quarter testQuarter = quarterList.get(quarterList.size() - 1);
        assertThat(testQuarter.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Quarter in Elasticsearch
        Quarter quarterEs = quarterSearchRepository.findOne(testQuarter.getId());
        assertThat(quarterEs).isEqualToIgnoringGivenFields(testQuarter);
    }

    @Test
    @Transactional
    public void updateNonExistingQuarter() throws Exception {
        int databaseSizeBeforeUpdate = quarterRepository.findAll().size();

        // Create the Quarter

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restQuarterMockMvc.perform(put("/api/quarters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quarter)))
            .andExpect(status().isCreated());

        // Validate the Quarter in the database
        List<Quarter> quarterList = quarterRepository.findAll();
        assertThat(quarterList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteQuarter() throws Exception {
        // Initialize the database
        quarterRepository.saveAndFlush(quarter);
        quarterSearchRepository.save(quarter);
        int databaseSizeBeforeDelete = quarterRepository.findAll().size();

        // Get the quarter
        restQuarterMockMvc.perform(delete("/api/quarters/{id}", quarter.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean quarterExistsInEs = quarterSearchRepository.exists(quarter.getId());
        assertThat(quarterExistsInEs).isFalse();

        // Validate the database is empty
        List<Quarter> quarterList = quarterRepository.findAll();
        assertThat(quarterList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchQuarter() throws Exception {
        // Initialize the database
        quarterRepository.saveAndFlush(quarter);
        quarterSearchRepository.save(quarter);

        // Search the quarter
        restQuarterMockMvc.perform(get("/api/_search/quarters?query=id:" + quarter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quarter.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Quarter.class);
        Quarter quarter1 = new Quarter();
        quarter1.setId(1L);
        Quarter quarter2 = new Quarter();
        quarter2.setId(quarter1.getId());
        assertThat(quarter1).isEqualTo(quarter2);
        quarter2.setId(2L);
        assertThat(quarter1).isNotEqualTo(quarter2);
        quarter1.setId(null);
        assertThat(quarter1).isNotEqualTo(quarter2);
    }
}
