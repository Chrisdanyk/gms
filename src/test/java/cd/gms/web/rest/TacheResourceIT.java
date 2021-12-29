package cd.gms.web.rest;

import static cd.gms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cd.gms.IntegrationTest;
import cd.gms.domain.Tache;
import cd.gms.repository.TacheRepository;
import java.math.BigDecimal;
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
 * Integration tests for the {@link TacheResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TacheResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRIX_UNITAIRE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRIX_UNITAIRE = new BigDecimal(2);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_DISPONIBLE = false;
    private static final Boolean UPDATED_DISPONIBLE = true;

    private static final String DEFAULT_NUID = "AAAAAAAAAA";
    private static final String UPDATED_NUID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/taches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTacheMockMvc;

    private Tache tache;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tache createEntity(EntityManager em) {
        Tache tache = new Tache()
            .nom(DEFAULT_NOM)
            .prixUnitaire(DEFAULT_PRIX_UNITAIRE)
            .description(DEFAULT_DESCRIPTION)
            .disponible(DEFAULT_DISPONIBLE)
            .nuid(DEFAULT_NUID);
        return tache;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tache createUpdatedEntity(EntityManager em) {
        Tache tache = new Tache()
            .nom(UPDATED_NOM)
            .prixUnitaire(UPDATED_PRIX_UNITAIRE)
            .description(UPDATED_DESCRIPTION)
            .disponible(UPDATED_DISPONIBLE)
            .nuid(UPDATED_NUID);
        return tache;
    }

    @BeforeEach
    public void initTest() {
        tache = createEntity(em);
    }

    @Test
    @Transactional
    void createTache() throws Exception {
        int databaseSizeBeforeCreate = tacheRepository.findAll().size();
        // Create the Tache
        restTacheMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tache)))
            .andExpect(status().isCreated());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeCreate + 1);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testTache.getPrixUnitaire()).isEqualByComparingTo(DEFAULT_PRIX_UNITAIRE);
        assertThat(testTache.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTache.getDisponible()).isEqualTo(DEFAULT_DISPONIBLE);
        assertThat(testTache.getNuid()).isEqualTo(DEFAULT_NUID);
    }

    @Test
    @Transactional
    void createTacheWithExistingId() throws Exception {
        // Create the Tache with an existing ID
        tache.setId(1L);

        int databaseSizeBeforeCreate = tacheRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTacheMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tache)))
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = tacheRepository.findAll().size();
        // set the field null
        tache.setNom(null);

        // Create the Tache, which fails.

        restTacheMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tache)))
            .andExpect(status().isBadRequest());

        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrixUnitaireIsRequired() throws Exception {
        int databaseSizeBeforeTest = tacheRepository.findAll().size();
        // set the field null
        tache.setPrixUnitaire(null);

        // Create the Tache, which fails.

        restTacheMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tache)))
            .andExpect(status().isBadRequest());

        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDisponibleIsRequired() throws Exception {
        int databaseSizeBeforeTest = tacheRepository.findAll().size();
        // set the field null
        tache.setDisponible(null);

        // Create the Tache, which fails.

        restTacheMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tache)))
            .andExpect(status().isBadRequest());

        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNuidIsRequired() throws Exception {
        int databaseSizeBeforeTest = tacheRepository.findAll().size();
        // set the field null
        tache.setNuid(null);

        // Create the Tache, which fails.

        restTacheMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tache)))
            .andExpect(status().isBadRequest());

        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTaches() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList
        restTacheMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tache.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prixUnitaire").value(hasItem(sameNumber(DEFAULT_PRIX_UNITAIRE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].disponible").value(hasItem(DEFAULT_DISPONIBLE.booleanValue())))
            .andExpect(jsonPath("$.[*].nuid").value(hasItem(DEFAULT_NUID)));
    }

    @Test
    @Transactional
    void getTache() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get the tache
        restTacheMockMvc
            .perform(get(ENTITY_API_URL_ID, tache.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tache.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prixUnitaire").value(sameNumber(DEFAULT_PRIX_UNITAIRE)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.disponible").value(DEFAULT_DISPONIBLE.booleanValue()))
            .andExpect(jsonPath("$.nuid").value(DEFAULT_NUID));
    }

    @Test
    @Transactional
    void getNonExistingTache() throws Exception {
        // Get the tache
        restTacheMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTache() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();

        // Update the tache
        Tache updatedTache = tacheRepository.findById(tache.getId()).get();
        // Disconnect from session so that the updates on updatedTache are not directly saved in db
        em.detach(updatedTache);
        updatedTache
            .nom(UPDATED_NOM)
            .prixUnitaire(UPDATED_PRIX_UNITAIRE)
            .description(UPDATED_DESCRIPTION)
            .disponible(UPDATED_DISPONIBLE)
            .nuid(UPDATED_NUID);

        restTacheMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTache.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTache))
            )
            .andExpect(status().isOk());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testTache.getPrixUnitaire()).isEqualTo(UPDATED_PRIX_UNITAIRE);
        assertThat(testTache.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTache.getDisponible()).isEqualTo(UPDATED_DISPONIBLE);
        assertThat(testTache.getNuid()).isEqualTo(UPDATED_NUID);
    }

    @Test
    @Transactional
    void putNonExistingTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();
        tache.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tache.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tache))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();
        tache.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tache))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();
        tache.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tache)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTacheWithPatch() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();

        // Update the tache using partial update
        Tache partialUpdatedTache = new Tache();
        partialUpdatedTache.setId(tache.getId());

        partialUpdatedTache.nom(UPDATED_NOM).prixUnitaire(UPDATED_PRIX_UNITAIRE);

        restTacheMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTache.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTache))
            )
            .andExpect(status().isOk());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testTache.getPrixUnitaire()).isEqualByComparingTo(UPDATED_PRIX_UNITAIRE);
        assertThat(testTache.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTache.getDisponible()).isEqualTo(DEFAULT_DISPONIBLE);
        assertThat(testTache.getNuid()).isEqualTo(DEFAULT_NUID);
    }

    @Test
    @Transactional
    void fullUpdateTacheWithPatch() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();

        // Update the tache using partial update
        Tache partialUpdatedTache = new Tache();
        partialUpdatedTache.setId(tache.getId());

        partialUpdatedTache
            .nom(UPDATED_NOM)
            .prixUnitaire(UPDATED_PRIX_UNITAIRE)
            .description(UPDATED_DESCRIPTION)
            .disponible(UPDATED_DISPONIBLE)
            .nuid(UPDATED_NUID);

        restTacheMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTache.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTache))
            )
            .andExpect(status().isOk());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testTache.getPrixUnitaire()).isEqualByComparingTo(UPDATED_PRIX_UNITAIRE);
        assertThat(testTache.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTache.getDisponible()).isEqualTo(UPDATED_DISPONIBLE);
        assertThat(testTache.getNuid()).isEqualTo(UPDATED_NUID);
    }

    @Test
    @Transactional
    void patchNonExistingTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();
        tache.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tache.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tache))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();
        tache.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tache))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();
        tache.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(tache)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTache() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        int databaseSizeBeforeDelete = tacheRepository.findAll().size();

        // Delete the tache
        restTacheMockMvc
            .perform(delete(ENTITY_API_URL_ID, tache.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
