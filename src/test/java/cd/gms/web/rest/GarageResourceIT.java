package cd.gms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cd.gms.IntegrationTest;
import cd.gms.domain.Garage;
import cd.gms.repository.GarageRepository;
import cd.gms.service.GarageService;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link GarageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class GarageResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESSE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String DEFAULT_RCCM = "AAAAAAAAAA";
    private static final String UPDATED_RCCM = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_NUID = "AAAAAAAAAA";
    private static final String UPDATED_NUID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/garages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GarageRepository garageRepository;

    @Mock
    private GarageRepository garageRepositoryMock;

    @Mock
    private GarageService garageServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGarageMockMvc;

    private Garage garage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Garage createEntity(EntityManager em) {
        Garage garage = new Garage()
            .nom(DEFAULT_NOM)
            .addresse(DEFAULT_ADDRESSE)
            .email(DEFAULT_EMAIL)
            .telephone(DEFAULT_TELEPHONE)
            .rccm(DEFAULT_RCCM)
            .url(DEFAULT_URL)
            .nuid(DEFAULT_NUID);
        return garage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Garage createUpdatedEntity(EntityManager em) {
        Garage garage = new Garage()
            .nom(UPDATED_NOM)
            .addresse(UPDATED_ADDRESSE)
            .email(UPDATED_EMAIL)
            .telephone(UPDATED_TELEPHONE)
            .rccm(UPDATED_RCCM)
            .url(UPDATED_URL)
            .nuid(UPDATED_NUID);
        return garage;
    }

    @BeforeEach
    public void initTest() {
        garage = createEntity(em);
    }

    @Test
    @Transactional
    void createGarage() throws Exception {
        int databaseSizeBeforeCreate = garageRepository.findAll().size();
        // Create the Garage
        restGarageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(garage)))
            .andExpect(status().isCreated());

        // Validate the Garage in the database
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeCreate + 1);
        Garage testGarage = garageList.get(garageList.size() - 1);
        assertThat(testGarage.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testGarage.getAddresse()).isEqualTo(DEFAULT_ADDRESSE);
        assertThat(testGarage.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testGarage.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
        assertThat(testGarage.getRccm()).isEqualTo(DEFAULT_RCCM);
        assertThat(testGarage.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testGarage.getNuid()).isEqualTo(DEFAULT_NUID);
    }

    @Test
    @Transactional
    void createGarageWithExistingId() throws Exception {
        // Create the Garage with an existing ID
        garage.setId(1L);

        int databaseSizeBeforeCreate = garageRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGarageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(garage)))
            .andExpect(status().isBadRequest());

        // Validate the Garage in the database
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = garageRepository.findAll().size();
        // set the field null
        garage.setNom(null);

        // Create the Garage, which fails.

        restGarageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(garage)))
            .andExpect(status().isBadRequest());

        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNuidIsRequired() throws Exception {
        int databaseSizeBeforeTest = garageRepository.findAll().size();
        // set the field null
        garage.setNuid(null);

        // Create the Garage, which fails.

        restGarageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(garage)))
            .andExpect(status().isBadRequest());

        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGarages() throws Exception {
        // Initialize the database
        garageRepository.saveAndFlush(garage);

        // Get all the garageList
        restGarageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(garage.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].addresse").value(hasItem(DEFAULT_ADDRESSE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].rccm").value(hasItem(DEFAULT_RCCM)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].nuid").value(hasItem(DEFAULT_NUID)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGaragesWithEagerRelationshipsIsEnabled() throws Exception {
        when(garageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGarageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(garageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGaragesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(garageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGarageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(garageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getGarage() throws Exception {
        // Initialize the database
        garageRepository.saveAndFlush(garage);

        // Get the garage
        restGarageMockMvc
            .perform(get(ENTITY_API_URL_ID, garage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(garage.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.addresse").value(DEFAULT_ADDRESSE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE))
            .andExpect(jsonPath("$.rccm").value(DEFAULT_RCCM))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.nuid").value(DEFAULT_NUID));
    }

    @Test
    @Transactional
    void getNonExistingGarage() throws Exception {
        // Get the garage
        restGarageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGarage() throws Exception {
        // Initialize the database
        garageRepository.saveAndFlush(garage);

        int databaseSizeBeforeUpdate = garageRepository.findAll().size();

        // Update the garage
        Garage updatedGarage = garageRepository.findById(garage.getId()).get();
        // Disconnect from session so that the updates on updatedGarage are not directly saved in db
        em.detach(updatedGarage);
        updatedGarage
            .nom(UPDATED_NOM)
            .addresse(UPDATED_ADDRESSE)
            .email(UPDATED_EMAIL)
            .telephone(UPDATED_TELEPHONE)
            .rccm(UPDATED_RCCM)
            .url(UPDATED_URL)
            .nuid(UPDATED_NUID);

        restGarageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGarage.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGarage))
            )
            .andExpect(status().isOk());

        // Validate the Garage in the database
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeUpdate);
        Garage testGarage = garageList.get(garageList.size() - 1);
        assertThat(testGarage.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testGarage.getAddresse()).isEqualTo(UPDATED_ADDRESSE);
        assertThat(testGarage.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testGarage.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testGarage.getRccm()).isEqualTo(UPDATED_RCCM);
        assertThat(testGarage.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testGarage.getNuid()).isEqualTo(UPDATED_NUID);
    }

    @Test
    @Transactional
    void putNonExistingGarage() throws Exception {
        int databaseSizeBeforeUpdate = garageRepository.findAll().size();
        garage.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGarageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, garage.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(garage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Garage in the database
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGarage() throws Exception {
        int databaseSizeBeforeUpdate = garageRepository.findAll().size();
        garage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGarageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(garage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Garage in the database
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGarage() throws Exception {
        int databaseSizeBeforeUpdate = garageRepository.findAll().size();
        garage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGarageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(garage)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Garage in the database
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGarageWithPatch() throws Exception {
        // Initialize the database
        garageRepository.saveAndFlush(garage);

        int databaseSizeBeforeUpdate = garageRepository.findAll().size();

        // Update the garage using partial update
        Garage partialUpdatedGarage = new Garage();
        partialUpdatedGarage.setId(garage.getId());

        partialUpdatedGarage.email(UPDATED_EMAIL).rccm(UPDATED_RCCM).nuid(UPDATED_NUID);

        restGarageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGarage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGarage))
            )
            .andExpect(status().isOk());

        // Validate the Garage in the database
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeUpdate);
        Garage testGarage = garageList.get(garageList.size() - 1);
        assertThat(testGarage.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testGarage.getAddresse()).isEqualTo(DEFAULT_ADDRESSE);
        assertThat(testGarage.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testGarage.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
        assertThat(testGarage.getRccm()).isEqualTo(UPDATED_RCCM);
        assertThat(testGarage.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testGarage.getNuid()).isEqualTo(UPDATED_NUID);
    }

    @Test
    @Transactional
    void fullUpdateGarageWithPatch() throws Exception {
        // Initialize the database
        garageRepository.saveAndFlush(garage);

        int databaseSizeBeforeUpdate = garageRepository.findAll().size();

        // Update the garage using partial update
        Garage partialUpdatedGarage = new Garage();
        partialUpdatedGarage.setId(garage.getId());

        partialUpdatedGarage
            .nom(UPDATED_NOM)
            .addresse(UPDATED_ADDRESSE)
            .email(UPDATED_EMAIL)
            .telephone(UPDATED_TELEPHONE)
            .rccm(UPDATED_RCCM)
            .url(UPDATED_URL)
            .nuid(UPDATED_NUID);

        restGarageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGarage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGarage))
            )
            .andExpect(status().isOk());

        // Validate the Garage in the database
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeUpdate);
        Garage testGarage = garageList.get(garageList.size() - 1);
        assertThat(testGarage.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testGarage.getAddresse()).isEqualTo(UPDATED_ADDRESSE);
        assertThat(testGarage.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testGarage.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testGarage.getRccm()).isEqualTo(UPDATED_RCCM);
        assertThat(testGarage.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testGarage.getNuid()).isEqualTo(UPDATED_NUID);
    }

    @Test
    @Transactional
    void patchNonExistingGarage() throws Exception {
        int databaseSizeBeforeUpdate = garageRepository.findAll().size();
        garage.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGarageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, garage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(garage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Garage in the database
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGarage() throws Exception {
        int databaseSizeBeforeUpdate = garageRepository.findAll().size();
        garage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGarageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(garage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Garage in the database
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGarage() throws Exception {
        int databaseSizeBeforeUpdate = garageRepository.findAll().size();
        garage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGarageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(garage)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Garage in the database
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGarage() throws Exception {
        // Initialize the database
        garageRepository.saveAndFlush(garage);

        int databaseSizeBeforeDelete = garageRepository.findAll().size();

        // Delete the garage
        restGarageMockMvc
            .perform(delete(ENTITY_API_URL_ID, garage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Garage> garageList = garageRepository.findAll();
        assertThat(garageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
