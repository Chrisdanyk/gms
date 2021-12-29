package cd.gms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cd.gms.IntegrationTest;
import cd.gms.domain.Engin;
import cd.gms.domain.enumeration.Type;
import cd.gms.repository.EnginRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EnginResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EnginResourceIT {

    private static final String DEFAULT_MODELE = "AAAAAAAAAA";
    private static final String UPDATED_MODELE = "BBBBBBBBBB";

    private static final String DEFAULT_PLAQUE = "AAAAAAAAAA";
    private static final String UPDATED_PLAQUE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_FABRICATION = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_FABRICATION = LocalDate.now(ZoneId.systemDefault());

    private static final Type DEFAULT_TYPE = Type.VOITURE;
    private static final Type UPDATED_TYPE = Type.CAMION;

    private static final String ENTITY_API_URL = "/api/engins";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EnginRepository enginRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnginMockMvc;

    private Engin engin;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Engin createEntity(EntityManager em) {
        Engin engin = new Engin()
            .modele(DEFAULT_MODELE)
            .plaque(DEFAULT_PLAQUE)
            .dateFabrication(DEFAULT_DATE_FABRICATION)
            .type(DEFAULT_TYPE);
        return engin;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Engin createUpdatedEntity(EntityManager em) {
        Engin engin = new Engin()
            .modele(UPDATED_MODELE)
            .plaque(UPDATED_PLAQUE)
            .dateFabrication(UPDATED_DATE_FABRICATION)
            .type(UPDATED_TYPE);
        return engin;
    }

    @BeforeEach
    public void initTest() {
        engin = createEntity(em);
    }

    @Test
    @Transactional
    void createEngin() throws Exception {
        int databaseSizeBeforeCreate = enginRepository.findAll().size();
        // Create the Engin
        restEnginMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(engin)))
            .andExpect(status().isCreated());

        // Validate the Engin in the database
        List<Engin> enginList = enginRepository.findAll();
        assertThat(enginList).hasSize(databaseSizeBeforeCreate + 1);
        Engin testEngin = enginList.get(enginList.size() - 1);
        assertThat(testEngin.getModele()).isEqualTo(DEFAULT_MODELE);
        assertThat(testEngin.getPlaque()).isEqualTo(DEFAULT_PLAQUE);
        assertThat(testEngin.getDateFabrication()).isEqualTo(DEFAULT_DATE_FABRICATION);
        assertThat(testEngin.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void createEnginWithExistingId() throws Exception {
        // Create the Engin with an existing ID
        engin.setId(1L);

        int databaseSizeBeforeCreate = enginRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnginMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(engin)))
            .andExpect(status().isBadRequest());

        // Validate the Engin in the database
        List<Engin> enginList = enginRepository.findAll();
        assertThat(enginList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPlaqueIsRequired() throws Exception {
        int databaseSizeBeforeTest = enginRepository.findAll().size();
        // set the field null
        engin.setPlaque(null);

        // Create the Engin, which fails.

        restEnginMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(engin)))
            .andExpect(status().isBadRequest());

        List<Engin> enginList = enginRepository.findAll();
        assertThat(enginList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = enginRepository.findAll().size();
        // set the field null
        engin.setType(null);

        // Create the Engin, which fails.

        restEnginMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(engin)))
            .andExpect(status().isBadRequest());

        List<Engin> enginList = enginRepository.findAll();
        assertThat(enginList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEngins() throws Exception {
        // Initialize the database
        enginRepository.saveAndFlush(engin);

        // Get all the enginList
        restEnginMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(engin.getId().intValue())))
            .andExpect(jsonPath("$.[*].modele").value(hasItem(DEFAULT_MODELE)))
            .andExpect(jsonPath("$.[*].plaque").value(hasItem(DEFAULT_PLAQUE)))
            .andExpect(jsonPath("$.[*].dateFabrication").value(hasItem(DEFAULT_DATE_FABRICATION.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    void getEngin() throws Exception {
        // Initialize the database
        enginRepository.saveAndFlush(engin);

        // Get the engin
        restEnginMockMvc
            .perform(get(ENTITY_API_URL_ID, engin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(engin.getId().intValue()))
            .andExpect(jsonPath("$.modele").value(DEFAULT_MODELE))
            .andExpect(jsonPath("$.plaque").value(DEFAULT_PLAQUE))
            .andExpect(jsonPath("$.dateFabrication").value(DEFAULT_DATE_FABRICATION.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingEngin() throws Exception {
        // Get the engin
        restEnginMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEngin() throws Exception {
        // Initialize the database
        enginRepository.saveAndFlush(engin);

        int databaseSizeBeforeUpdate = enginRepository.findAll().size();

        // Update the engin
        Engin updatedEngin = enginRepository.findById(engin.getId()).get();
        // Disconnect from session so that the updates on updatedEngin are not directly saved in db
        em.detach(updatedEngin);
        updatedEngin.modele(UPDATED_MODELE).plaque(UPDATED_PLAQUE).dateFabrication(UPDATED_DATE_FABRICATION).type(UPDATED_TYPE);

        restEnginMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEngin.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEngin))
            )
            .andExpect(status().isOk());

        // Validate the Engin in the database
        List<Engin> enginList = enginRepository.findAll();
        assertThat(enginList).hasSize(databaseSizeBeforeUpdate);
        Engin testEngin = enginList.get(enginList.size() - 1);
        assertThat(testEngin.getModele()).isEqualTo(UPDATED_MODELE);
        assertThat(testEngin.getPlaque()).isEqualTo(UPDATED_PLAQUE);
        assertThat(testEngin.getDateFabrication()).isEqualTo(UPDATED_DATE_FABRICATION);
        assertThat(testEngin.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingEngin() throws Exception {
        int databaseSizeBeforeUpdate = enginRepository.findAll().size();
        engin.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnginMockMvc
            .perform(
                put(ENTITY_API_URL_ID, engin.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(engin))
            )
            .andExpect(status().isBadRequest());

        // Validate the Engin in the database
        List<Engin> enginList = enginRepository.findAll();
        assertThat(enginList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEngin() throws Exception {
        int databaseSizeBeforeUpdate = enginRepository.findAll().size();
        engin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnginMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(engin))
            )
            .andExpect(status().isBadRequest());

        // Validate the Engin in the database
        List<Engin> enginList = enginRepository.findAll();
        assertThat(enginList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEngin() throws Exception {
        int databaseSizeBeforeUpdate = enginRepository.findAll().size();
        engin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnginMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(engin)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Engin in the database
        List<Engin> enginList = enginRepository.findAll();
        assertThat(enginList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEnginWithPatch() throws Exception {
        // Initialize the database
        enginRepository.saveAndFlush(engin);

        int databaseSizeBeforeUpdate = enginRepository.findAll().size();

        // Update the engin using partial update
        Engin partialUpdatedEngin = new Engin();
        partialUpdatedEngin.setId(engin.getId());

        partialUpdatedEngin.plaque(UPDATED_PLAQUE).type(UPDATED_TYPE);

        restEnginMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEngin.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEngin))
            )
            .andExpect(status().isOk());

        // Validate the Engin in the database
        List<Engin> enginList = enginRepository.findAll();
        assertThat(enginList).hasSize(databaseSizeBeforeUpdate);
        Engin testEngin = enginList.get(enginList.size() - 1);
        assertThat(testEngin.getModele()).isEqualTo(DEFAULT_MODELE);
        assertThat(testEngin.getPlaque()).isEqualTo(UPDATED_PLAQUE);
        assertThat(testEngin.getDateFabrication()).isEqualTo(DEFAULT_DATE_FABRICATION);
        assertThat(testEngin.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateEnginWithPatch() throws Exception {
        // Initialize the database
        enginRepository.saveAndFlush(engin);

        int databaseSizeBeforeUpdate = enginRepository.findAll().size();

        // Update the engin using partial update
        Engin partialUpdatedEngin = new Engin();
        partialUpdatedEngin.setId(engin.getId());

        partialUpdatedEngin.modele(UPDATED_MODELE).plaque(UPDATED_PLAQUE).dateFabrication(UPDATED_DATE_FABRICATION).type(UPDATED_TYPE);

        restEnginMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEngin.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEngin))
            )
            .andExpect(status().isOk());

        // Validate the Engin in the database
        List<Engin> enginList = enginRepository.findAll();
        assertThat(enginList).hasSize(databaseSizeBeforeUpdate);
        Engin testEngin = enginList.get(enginList.size() - 1);
        assertThat(testEngin.getModele()).isEqualTo(UPDATED_MODELE);
        assertThat(testEngin.getPlaque()).isEqualTo(UPDATED_PLAQUE);
        assertThat(testEngin.getDateFabrication()).isEqualTo(UPDATED_DATE_FABRICATION);
        assertThat(testEngin.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingEngin() throws Exception {
        int databaseSizeBeforeUpdate = enginRepository.findAll().size();
        engin.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnginMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, engin.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(engin))
            )
            .andExpect(status().isBadRequest());

        // Validate the Engin in the database
        List<Engin> enginList = enginRepository.findAll();
        assertThat(enginList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEngin() throws Exception {
        int databaseSizeBeforeUpdate = enginRepository.findAll().size();
        engin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnginMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(engin))
            )
            .andExpect(status().isBadRequest());

        // Validate the Engin in the database
        List<Engin> enginList = enginRepository.findAll();
        assertThat(enginList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEngin() throws Exception {
        int databaseSizeBeforeUpdate = enginRepository.findAll().size();
        engin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnginMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(engin)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Engin in the database
        List<Engin> enginList = enginRepository.findAll();
        assertThat(enginList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEngin() throws Exception {
        // Initialize the database
        enginRepository.saveAndFlush(engin);

        int databaseSizeBeforeDelete = enginRepository.findAll().size();

        // Delete the engin
        restEnginMockMvc
            .perform(delete(ENTITY_API_URL_ID, engin.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Engin> enginList = enginRepository.findAll();
        assertThat(enginList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
