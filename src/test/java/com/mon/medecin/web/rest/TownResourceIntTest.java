package com.mon.medecin.web.rest;

import com.mon.medecin.MonMedecinApp;

import com.mon.medecin.domain.Town;
import com.mon.medecin.repository.TownRepository;
import com.mon.medecin.repository.search.TownSearchRepository;
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
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.mon.medecin.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TownResource REST controller.
 *
 * @see TownResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MonMedecinApp.class)
public class TownResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private TownSearchRepository townSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTownMockMvc;

    private Town town;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TownResource townResource = new TownResource(townRepository, townSearchRepository);
        this.restTownMockMvc = MockMvcBuilders.standaloneSetup(townResource)
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
    public static Town createEntity(EntityManager em) {
        Town town = new Town()
            .name(DEFAULT_NAME)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
        return town;
    }

    @Before
    public void initTest() {
        townSearchRepository.deleteAll();
        town = createEntity(em);
    }

    @Test
    @Transactional
    public void createTown() throws Exception {
        int databaseSizeBeforeCreate = townRepository.findAll().size();

        // Create the Town
        restTownMockMvc.perform(post("/api/towns")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(town)))
            .andExpect(status().isCreated());

        // Validate the Town in the database
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeCreate + 1);
        Town testTown = townList.get(townList.size() - 1);
        assertThat(testTown.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTown.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testTown.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);

        // Validate the Town in Elasticsearch
        Town townEs = townSearchRepository.findOne(testTown.getId());
        assertThat(townEs).isEqualToIgnoringGivenFields(testTown);
    }

    @Test
    @Transactional
    public void createTownWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = townRepository.findAll().size();

        // Create the Town with an existing ID
        town.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTownMockMvc.perform(post("/api/towns")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(town)))
            .andExpect(status().isBadRequest());

        // Validate the Town in the database
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = townRepository.findAll().size();
        // set the field null
        town.setName(null);

        // Create the Town, which fails.

        restTownMockMvc.perform(post("/api/towns")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(town)))
            .andExpect(status().isBadRequest());

        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTowns() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList
        restTownMockMvc.perform(get("/api/towns?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(town.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    public void getTown() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get the town
        restTownMockMvc.perform(get("/api/towns/{id}", town.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(town.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    public void getNonExistingTown() throws Exception {
        // Get the town
        restTownMockMvc.perform(get("/api/towns/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTown() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);
        townSearchRepository.save(town);
        int databaseSizeBeforeUpdate = townRepository.findAll().size();

        // Update the town
        Town updatedTown = townRepository.findOne(town.getId());
        // Disconnect from session so that the updates on updatedTown are not directly saved in db
        em.detach(updatedTown);
        updatedTown
            .name(UPDATED_NAME)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restTownMockMvc.perform(put("/api/towns")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTown)))
            .andExpect(status().isOk());

        // Validate the Town in the database
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeUpdate);
        Town testTown = townList.get(townList.size() - 1);
        assertThat(testTown.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTown.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testTown.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);

        // Validate the Town in Elasticsearch
        Town townEs = townSearchRepository.findOne(testTown.getId());
        assertThat(townEs).isEqualToIgnoringGivenFields(testTown);
    }

    @Test
    @Transactional
    public void updateNonExistingTown() throws Exception {
        int databaseSizeBeforeUpdate = townRepository.findAll().size();

        // Create the Town

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTownMockMvc.perform(put("/api/towns")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(town)))
            .andExpect(status().isCreated());

        // Validate the Town in the database
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTown() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);
        townSearchRepository.save(town);
        int databaseSizeBeforeDelete = townRepository.findAll().size();

        // Get the town
        restTownMockMvc.perform(delete("/api/towns/{id}", town.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean townExistsInEs = townSearchRepository.exists(town.getId());
        assertThat(townExistsInEs).isFalse();

        // Validate the database is empty
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTown() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);
        townSearchRepository.save(town);

        // Search the town
        restTownMockMvc.perform(get("/api/_search/towns?query=id:" + town.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(town.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Town.class);
        Town town1 = new Town();
        town1.setId(1L);
        Town town2 = new Town();
        town2.setId(town1.getId());
        assertThat(town1).isEqualTo(town2);
        town2.setId(2L);
        assertThat(town1).isNotEqualTo(town2);
        town1.setId(null);
        assertThat(town1).isNotEqualTo(town2);
    }
}
