package cd.gms.web.rest;

import static cd.gms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cd.gms.IntegrationTest;
import cd.gms.domain.Maintenance;
import cd.gms.repository.MaintenanceRepository;
import cd.gms.service.MaintenanceService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link MaintenanceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MaintenanceResourceIT {

    private static final LocalDate DEFAULT_DATE_DEBUT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_DEBUT = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATE_FIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_FIN = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_RAPPORT_GLOBAL = "AAAAAAAAAA";
    private static final String UPDATED_RAPPORT_GLOBAL = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRIX_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRIX_TOTAL = new BigDecimal(2);

    private static final BigDecimal DEFAULT_DISCOUNT_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_DISCOUNT_TOTAL = new BigDecimal(2);

    private static final String DEFAULT_NUID = "AAAAAAAAAA";
    private static final String UPDATED_NUID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/maintenances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Mock
    private MaintenanceRepository maintenanceRepositoryMock;

    @Mock
    private MaintenanceService maintenanceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMaintenanceMockMvc;

    private Maintenance maintenance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Maintenance createEntity(EntityManager em) {
        Maintenance maintenance = new Maintenance()
            .dateDebut(DEFAULT_DATE_DEBUT)
            .dateFin(DEFAULT_DATE_FIN)
            .rapportGlobal(DEFAULT_RAPPORT_GLOBAL)
            .prixTotal(DEFAULT_PRIX_TOTAL)
            .discountTotal(DEFAULT_DISCOUNT_TOTAL)
            .nuid(DEFAULT_NUID);
        return maintenance;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Maintenance createUpdatedEntity(EntityManager em) {
        Maintenance maintenance = new Maintenance()
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN)
            .rapportGlobal(UPDATED_RAPPORT_GLOBAL)
            .prixTotal(UPDATED_PRIX_TOTAL)
            .discountTotal(UPDATED_DISCOUNT_TOTAL)
            .nuid(UPDATED_NUID);
        return maintenance;
    }

    @BeforeEach
    public void initTest() {
        maintenance = createEntity(em);
    }

    @Test
    @Transactional
    void createMaintenance() throws Exception {
        int databaseSizeBeforeCreate = maintenanceRepository.findAll().size();
        // Create the Maintenance
        restMaintenanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(maintenance)))
            .andExpect(status().isCreated());

        // Validate the Maintenance in the database
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeCreate + 1);
        Maintenance testMaintenance = maintenanceList.get(maintenanceList.size() - 1);
        assertThat(testMaintenance.getDateDebut()).isEqualTo(DEFAULT_DATE_DEBUT);
        assertThat(testMaintenance.getDateFin()).isEqualTo(DEFAULT_DATE_FIN);
        assertThat(testMaintenance.getRapportGlobal()).isEqualTo(DEFAULT_RAPPORT_GLOBAL);
        assertThat(testMaintenance.getPrixTotal()).isEqualByComparingTo(DEFAULT_PRIX_TOTAL);
        assertThat(testMaintenance.getDiscountTotal()).isEqualByComparingTo(DEFAULT_DISCOUNT_TOTAL);
        assertThat(testMaintenance.getNuid()).isEqualTo(DEFAULT_NUID);
    }

    @Test
    @Transactional
    void createMaintenanceWithExistingId() throws Exception {
        // Create the Maintenance with an existing ID
        maintenance.setId(1L);

        int databaseSizeBeforeCreate = maintenanceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMaintenanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(maintenance)))
            .andExpect(status().isBadRequest());

        // Validate the Maintenance in the database
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateDebutIsRequired() throws Exception {
        int databaseSizeBeforeTest = maintenanceRepository.findAll().size();
        // set the field null
        maintenance.setDateDebut(null);

        // Create the Maintenance, which fails.

        restMaintenanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(maintenance)))
            .andExpect(status().isBadRequest());

        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateFinIsRequired() throws Exception {
        int databaseSizeBeforeTest = maintenanceRepository.findAll().size();
        // set the field null
        maintenance.setDateFin(null);

        // Create the Maintenance, which fails.

        restMaintenanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(maintenance)))
            .andExpect(status().isBadRequest());

        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrixTotalIsRequired() throws Exception {
        int databaseSizeBeforeTest = maintenanceRepository.findAll().size();
        // set the field null
        maintenance.setPrixTotal(null);

        // Create the Maintenance, which fails.

        restMaintenanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(maintenance)))
            .andExpect(status().isBadRequest());

        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNuidIsRequired() throws Exception {
        int databaseSizeBeforeTest = maintenanceRepository.findAll().size();
        // set the field null
        maintenance.setNuid(null);

        // Create the Maintenance, which fails.

        restMaintenanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(maintenance)))
            .andExpect(status().isBadRequest());

        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMaintenances() throws Exception {
        // Initialize the database
        maintenanceRepository.saveAndFlush(maintenance);

        // Get all the maintenanceList
        restMaintenanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(maintenance.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].dateFin").value(hasItem(DEFAULT_DATE_FIN.toString())))
            .andExpect(jsonPath("$.[*].rapportGlobal").value(hasItem(DEFAULT_RAPPORT_GLOBAL.toString())))
            .andExpect(jsonPath("$.[*].prixTotal").value(hasItem(sameNumber(DEFAULT_PRIX_TOTAL))))
            .andExpect(jsonPath("$.[*].discountTotal").value(hasItem(sameNumber(DEFAULT_DISCOUNT_TOTAL))))
            .andExpect(jsonPath("$.[*].nuid").value(hasItem(DEFAULT_NUID)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMaintenancesWithEagerRelationshipsIsEnabled() throws Exception {
        when(maintenanceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMaintenanceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(maintenanceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMaintenancesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(maintenanceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMaintenanceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(maintenanceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getMaintenance() throws Exception {
        // Initialize the database
        maintenanceRepository.saveAndFlush(maintenance);

        // Get the maintenance
        restMaintenanceMockMvc
            .perform(get(ENTITY_API_URL_ID, maintenance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(maintenance.getId().intValue()))
            .andExpect(jsonPath("$.dateDebut").value(DEFAULT_DATE_DEBUT.toString()))
            .andExpect(jsonPath("$.dateFin").value(DEFAULT_DATE_FIN.toString()))
            .andExpect(jsonPath("$.rapportGlobal").value(DEFAULT_RAPPORT_GLOBAL.toString()))
            .andExpect(jsonPath("$.prixTotal").value(sameNumber(DEFAULT_PRIX_TOTAL)))
            .andExpect(jsonPath("$.discountTotal").value(sameNumber(DEFAULT_DISCOUNT_TOTAL)))
            .andExpect(jsonPath("$.nuid").value(DEFAULT_NUID));
    }

    @Test
    @Transactional
    void getNonExistingMaintenance() throws Exception {
        // Get the maintenance
        restMaintenanceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewMaintenance() throws Exception {
        // Initialize the database
        maintenanceRepository.saveAndFlush(maintenance);

        int databaseSizeBeforeUpdate = maintenanceRepository.findAll().size();

        // Update the maintenance
        Maintenance updatedMaintenance = maintenanceRepository.findById(maintenance.getId()).get();
        // Disconnect from session so that the updates on updatedMaintenance are not directly saved in db
        em.detach(updatedMaintenance);
        updatedMaintenance
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN)
            .rapportGlobal(UPDATED_RAPPORT_GLOBAL)
            .prixTotal(UPDATED_PRIX_TOTAL)
            .discountTotal(UPDATED_DISCOUNT_TOTAL)
            .nuid(UPDATED_NUID);

        restMaintenanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMaintenance.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedMaintenance))
            )
            .andExpect(status().isOk());

        // Validate the Maintenance in the database
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeUpdate);
        Maintenance testMaintenance = maintenanceList.get(maintenanceList.size() - 1);
        assertThat(testMaintenance.getDateDebut()).isEqualTo(UPDATED_DATE_DEBUT);
        assertThat(testMaintenance.getDateFin()).isEqualTo(UPDATED_DATE_FIN);
        assertThat(testMaintenance.getRapportGlobal()).isEqualTo(UPDATED_RAPPORT_GLOBAL);
        assertThat(testMaintenance.getPrixTotal()).isEqualTo(UPDATED_PRIX_TOTAL);
        assertThat(testMaintenance.getDiscountTotal()).isEqualTo(UPDATED_DISCOUNT_TOTAL);
        assertThat(testMaintenance.getNuid()).isEqualTo(UPDATED_NUID);
    }

    @Test
    @Transactional
    void putNonExistingMaintenance() throws Exception {
        int databaseSizeBeforeUpdate = maintenanceRepository.findAll().size();
        maintenance.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaintenanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, maintenance.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(maintenance))
            )
            .andExpect(status().isBadRequest());

        // Validate the Maintenance in the database
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMaintenance() throws Exception {
        int databaseSizeBeforeUpdate = maintenanceRepository.findAll().size();
        maintenance.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaintenanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(maintenance))
            )
            .andExpect(status().isBadRequest());

        // Validate the Maintenance in the database
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMaintenance() throws Exception {
        int databaseSizeBeforeUpdate = maintenanceRepository.findAll().size();
        maintenance.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaintenanceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(maintenance)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Maintenance in the database
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMaintenanceWithPatch() throws Exception {
        // Initialize the database
        maintenanceRepository.saveAndFlush(maintenance);

        int databaseSizeBeforeUpdate = maintenanceRepository.findAll().size();

        // Update the maintenance using partial update
        Maintenance partialUpdatedMaintenance = new Maintenance();
        partialUpdatedMaintenance.setId(maintenance.getId());

        partialUpdatedMaintenance
            .dateDebut(UPDATED_DATE_DEBUT)
            .rapportGlobal(UPDATED_RAPPORT_GLOBAL)
            .prixTotal(UPDATED_PRIX_TOTAL)
            .discountTotal(UPDATED_DISCOUNT_TOTAL)
            .nuid(UPDATED_NUID);

        restMaintenanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMaintenance.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMaintenance))
            )
            .andExpect(status().isOk());

        // Validate the Maintenance in the database
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeUpdate);
        Maintenance testMaintenance = maintenanceList.get(maintenanceList.size() - 1);
        assertThat(testMaintenance.getDateDebut()).isEqualTo(UPDATED_DATE_DEBUT);
        assertThat(testMaintenance.getDateFin()).isEqualTo(DEFAULT_DATE_FIN);
        assertThat(testMaintenance.getRapportGlobal()).isEqualTo(UPDATED_RAPPORT_GLOBAL);
        assertThat(testMaintenance.getPrixTotal()).isEqualByComparingTo(UPDATED_PRIX_TOTAL);
        assertThat(testMaintenance.getDiscountTotal()).isEqualByComparingTo(UPDATED_DISCOUNT_TOTAL);
        assertThat(testMaintenance.getNuid()).isEqualTo(UPDATED_NUID);
    }

    @Test
    @Transactional
    void fullUpdateMaintenanceWithPatch() throws Exception {
        // Initialize the database
        maintenanceRepository.saveAndFlush(maintenance);

        int databaseSizeBeforeUpdate = maintenanceRepository.findAll().size();

        // Update the maintenance using partial update
        Maintenance partialUpdatedMaintenance = new Maintenance();
        partialUpdatedMaintenance.setId(maintenance.getId());

        partialUpdatedMaintenance
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN)
            .rapportGlobal(UPDATED_RAPPORT_GLOBAL)
            .prixTotal(UPDATED_PRIX_TOTAL)
            .discountTotal(UPDATED_DISCOUNT_TOTAL)
            .nuid(UPDATED_NUID);

        restMaintenanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMaintenance.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMaintenance))
            )
            .andExpect(status().isOk());

        // Validate the Maintenance in the database
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeUpdate);
        Maintenance testMaintenance = maintenanceList.get(maintenanceList.size() - 1);
        assertThat(testMaintenance.getDateDebut()).isEqualTo(UPDATED_DATE_DEBUT);
        assertThat(testMaintenance.getDateFin()).isEqualTo(UPDATED_DATE_FIN);
        assertThat(testMaintenance.getRapportGlobal()).isEqualTo(UPDATED_RAPPORT_GLOBAL);
        assertThat(testMaintenance.getPrixTotal()).isEqualByComparingTo(UPDATED_PRIX_TOTAL);
        assertThat(testMaintenance.getDiscountTotal()).isEqualByComparingTo(UPDATED_DISCOUNT_TOTAL);
        assertThat(testMaintenance.getNuid()).isEqualTo(UPDATED_NUID);
    }

    @Test
    @Transactional
    void patchNonExistingMaintenance() throws Exception {
        int databaseSizeBeforeUpdate = maintenanceRepository.findAll().size();
        maintenance.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaintenanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, maintenance.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(maintenance))
            )
            .andExpect(status().isBadRequest());

        // Validate the Maintenance in the database
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMaintenance() throws Exception {
        int databaseSizeBeforeUpdate = maintenanceRepository.findAll().size();
        maintenance.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaintenanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(maintenance))
            )
            .andExpect(status().isBadRequest());

        // Validate the Maintenance in the database
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMaintenance() throws Exception {
        int databaseSizeBeforeUpdate = maintenanceRepository.findAll().size();
        maintenance.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaintenanceMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(maintenance))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Maintenance in the database
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMaintenance() throws Exception {
        // Initialize the database
        maintenanceRepository.saveAndFlush(maintenance);

        int databaseSizeBeforeDelete = maintenanceRepository.findAll().size();

        // Delete the maintenance
        restMaintenanceMockMvc
            .perform(delete(ENTITY_API_URL_ID, maintenance.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
