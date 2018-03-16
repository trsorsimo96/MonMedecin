package com.mon.medecin.web.rest;

import com.mon.medecin.MonMedecinApp;

import com.mon.medecin.domain.Hospital;
import com.mon.medecin.repository.HospitalRepository;
import com.mon.medecin.repository.search.HospitalSearchRepository;
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
 * Test class for the HospitalResource REST controller.
 *
 * @see HospitalResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MonMedecinApp.class)
public class HospitalResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LOCALITY = "AAAAAAAAAA";
    private static final String UPDATED_LOCALITY = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final Long DEFAULT_LATTITUDE = 1L;
    private static final Long UPDATED_LATTITUDE = 2L;

    private static final Long DEFAULT_LONGITUDE = 1L;
    private static final Long UPDATED_LONGITUDE = 2L;

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private HospitalSearchRepository hospitalSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restHospitalMockMvc;

    private Hospital hospital;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final HospitalResource hospitalResource = new HospitalResource(hospitalRepository, hospitalSearchRepository);
        this.restHospitalMockMvc = MockMvcBuilders.standaloneSetup(hospitalResource)
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
    public static Hospital createEntity(EntityManager em) {
        Hospital hospital = new Hospital()
            .name(DEFAULT_NAME)
            .locality(DEFAULT_LOCALITY)
            .phone(DEFAULT_PHONE)
            .email(DEFAULT_EMAIL)
            .lattitude(DEFAULT_LATTITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
        return hospital;
    }

    @Before
    public void initTest() {
        hospitalSearchRepository.deleteAll();
        hospital = createEntity(em);
    }

    @Test
    @Transactional
    public void createHospital() throws Exception {
        int databaseSizeBeforeCreate = hospitalRepository.findAll().size();

        // Create the Hospital
        restHospitalMockMvc.perform(post("/api/hospitals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hospital)))
            .andExpect(status().isCreated());

        // Validate the Hospital in the database
        List<Hospital> hospitalList = hospitalRepository.findAll();
        assertThat(hospitalList).hasSize(databaseSizeBeforeCreate + 1);
        Hospital testHospital = hospitalList.get(hospitalList.size() - 1);
        assertThat(testHospital.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testHospital.getLocality()).isEqualTo(DEFAULT_LOCALITY);
        assertThat(testHospital.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testHospital.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testHospital.getLattitude()).isEqualTo(DEFAULT_LATTITUDE);
        assertThat(testHospital.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testHospital.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testHospital.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);

        // Validate the Hospital in Elasticsearch
        Hospital hospitalEs = hospitalSearchRepository.findOne(testHospital.getId());
        assertThat(hospitalEs).isEqualToIgnoringGivenFields(testHospital);
    }

    @Test
    @Transactional
    public void createHospitalWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = hospitalRepository.findAll().size();

        // Create the Hospital with an existing ID
        hospital.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restHospitalMockMvc.perform(post("/api/hospitals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hospital)))
            .andExpect(status().isBadRequest());

        // Validate the Hospital in the database
        List<Hospital> hospitalList = hospitalRepository.findAll();
        assertThat(hospitalList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = hospitalRepository.findAll().size();
        // set the field null
        hospital.setName(null);

        // Create the Hospital, which fails.

        restHospitalMockMvc.perform(post("/api/hospitals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hospital)))
            .andExpect(status().isBadRequest());

        List<Hospital> hospitalList = hospitalRepository.findAll();
        assertThat(hospitalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllHospitals() throws Exception {
        // Initialize the database
        hospitalRepository.saveAndFlush(hospital);

        // Get all the hospitalList
        restHospitalMockMvc.perform(get("/api/hospitals?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hospital.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].locality").value(hasItem(DEFAULT_LOCALITY.toString())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].lattitude").value(hasItem(DEFAULT_LATTITUDE.intValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.intValue())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    public void getHospital() throws Exception {
        // Initialize the database
        hospitalRepository.saveAndFlush(hospital);

        // Get the hospital
        restHospitalMockMvc.perform(get("/api/hospitals/{id}", hospital.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(hospital.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.locality").value(DEFAULT_LOCALITY.toString()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.lattitude").value(DEFAULT_LATTITUDE.intValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.intValue()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    public void getNonExistingHospital() throws Exception {
        // Get the hospital
        restHospitalMockMvc.perform(get("/api/hospitals/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateHospital() throws Exception {
        // Initialize the database
        hospitalRepository.saveAndFlush(hospital);
        hospitalSearchRepository.save(hospital);
        int databaseSizeBeforeUpdate = hospitalRepository.findAll().size();

        // Update the hospital
        Hospital updatedHospital = hospitalRepository.findOne(hospital.getId());
        // Disconnect from session so that the updates on updatedHospital are not directly saved in db
        em.detach(updatedHospital);
        updatedHospital
            .name(UPDATED_NAME)
            .locality(UPDATED_LOCALITY)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL)
            .lattitude(UPDATED_LATTITUDE)
            .longitude(UPDATED_LONGITUDE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restHospitalMockMvc.perform(put("/api/hospitals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedHospital)))
            .andExpect(status().isOk());

        // Validate the Hospital in the database
        List<Hospital> hospitalList = hospitalRepository.findAll();
        assertThat(hospitalList).hasSize(databaseSizeBeforeUpdate);
        Hospital testHospital = hospitalList.get(hospitalList.size() - 1);
        assertThat(testHospital.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testHospital.getLocality()).isEqualTo(UPDATED_LOCALITY);
        assertThat(testHospital.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testHospital.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testHospital.getLattitude()).isEqualTo(UPDATED_LATTITUDE);
        assertThat(testHospital.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testHospital.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testHospital.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);

        // Validate the Hospital in Elasticsearch
        Hospital hospitalEs = hospitalSearchRepository.findOne(testHospital.getId());
        assertThat(hospitalEs).isEqualToIgnoringGivenFields(testHospital);
    }

    @Test
    @Transactional
    public void updateNonExistingHospital() throws Exception {
        int databaseSizeBeforeUpdate = hospitalRepository.findAll().size();

        // Create the Hospital

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restHospitalMockMvc.perform(put("/api/hospitals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hospital)))
            .andExpect(status().isCreated());

        // Validate the Hospital in the database
        List<Hospital> hospitalList = hospitalRepository.findAll();
        assertThat(hospitalList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteHospital() throws Exception {
        // Initialize the database
        hospitalRepository.saveAndFlush(hospital);
        hospitalSearchRepository.save(hospital);
        int databaseSizeBeforeDelete = hospitalRepository.findAll().size();

        // Get the hospital
        restHospitalMockMvc.perform(delete("/api/hospitals/{id}", hospital.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean hospitalExistsInEs = hospitalSearchRepository.exists(hospital.getId());
        assertThat(hospitalExistsInEs).isFalse();

        // Validate the database is empty
        List<Hospital> hospitalList = hospitalRepository.findAll();
        assertThat(hospitalList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchHospital() throws Exception {
        // Initialize the database
        hospitalRepository.saveAndFlush(hospital);
        hospitalSearchRepository.save(hospital);

        // Search the hospital
        restHospitalMockMvc.perform(get("/api/_search/hospitals?query=id:" + hospital.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hospital.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].locality").value(hasItem(DEFAULT_LOCALITY.toString())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].lattitude").value(hasItem(DEFAULT_LATTITUDE.intValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.intValue())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Hospital.class);
        Hospital hospital1 = new Hospital();
        hospital1.setId(1L);
        Hospital hospital2 = new Hospital();
        hospital2.setId(hospital1.getId());
        assertThat(hospital1).isEqualTo(hospital2);
        hospital2.setId(2L);
        assertThat(hospital1).isNotEqualTo(hospital2);
        hospital1.setId(null);
        assertThat(hospital1).isNotEqualTo(hospital2);
    }
}
