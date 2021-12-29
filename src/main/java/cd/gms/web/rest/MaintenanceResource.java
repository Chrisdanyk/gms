package cd.gms.web.rest;

import cd.gms.domain.Maintenance;
import cd.gms.repository.MaintenanceRepository;
import cd.gms.service.MaintenanceService;
import cd.gms.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link cd.gms.domain.Maintenance}.
 */
@RestController
@RequestMapping("/api")
public class MaintenanceResource {

    private final Logger log = LoggerFactory.getLogger(MaintenanceResource.class);

    private static final String ENTITY_NAME = "maintenance";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MaintenanceService maintenanceService;

    private final MaintenanceRepository maintenanceRepository;

    public MaintenanceResource(MaintenanceService maintenanceService, MaintenanceRepository maintenanceRepository) {
        this.maintenanceService = maintenanceService;
        this.maintenanceRepository = maintenanceRepository;
    }

    /**
     * {@code POST  /maintenances} : Create a new maintenance.
     *
     * @param maintenance the maintenance to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new maintenance, or with status {@code 400 (Bad Request)} if the maintenance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/maintenances")
    public ResponseEntity<Maintenance> createMaintenance(@Valid @RequestBody Maintenance maintenance) throws URISyntaxException {
        log.debug("REST request to save Maintenance : {}", maintenance);
        if (maintenance.getId() != null) {
            throw new BadRequestAlertException("A new maintenance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Maintenance result = maintenanceService.save(maintenance);
        return ResponseEntity
            .created(new URI("/api/maintenances/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /maintenances/:id} : Updates an existing maintenance.
     *
     * @param id the id of the maintenance to save.
     * @param maintenance the maintenance to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated maintenance,
     * or with status {@code 400 (Bad Request)} if the maintenance is not valid,
     * or with status {@code 500 (Internal Server Error)} if the maintenance couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/maintenances/{id}")
    public ResponseEntity<Maintenance> updateMaintenance(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Maintenance maintenance
    ) throws URISyntaxException {
        log.debug("REST request to update Maintenance : {}, {}", id, maintenance);
        if (maintenance.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, maintenance.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!maintenanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Maintenance result = maintenanceService.save(maintenance);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, maintenance.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /maintenances/:id} : Partial updates given fields of an existing maintenance, field will ignore if it is null
     *
     * @param id the id of the maintenance to save.
     * @param maintenance the maintenance to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated maintenance,
     * or with status {@code 400 (Bad Request)} if the maintenance is not valid,
     * or with status {@code 404 (Not Found)} if the maintenance is not found,
     * or with status {@code 500 (Internal Server Error)} if the maintenance couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/maintenances/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Maintenance> partialUpdateMaintenance(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Maintenance maintenance
    ) throws URISyntaxException {
        log.debug("REST request to partial update Maintenance partially : {}, {}", id, maintenance);
        if (maintenance.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, maintenance.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!maintenanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Maintenance> result = maintenanceService.partialUpdate(maintenance);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, maintenance.getId().toString())
        );
    }

    /**
     * {@code GET  /maintenances} : get all the maintenances.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of maintenances in body.
     */
    @GetMapping("/maintenances")
    public ResponseEntity<List<Maintenance>> getAllMaintenances(
        Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Maintenances");
        Page<Maintenance> page;
        if (eagerload) {
            page = maintenanceService.findAllWithEagerRelationships(pageable);
        } else {
            page = maintenanceService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /maintenances/:id} : get the "id" maintenance.
     *
     * @param id the id of the maintenance to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the maintenance, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/maintenances/{id}")
    public ResponseEntity<Maintenance> getMaintenance(@PathVariable Long id) {
        log.debug("REST request to get Maintenance : {}", id);
        Optional<Maintenance> maintenance = maintenanceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(maintenance);
    }

    /**
     * {@code DELETE  /maintenances/:id} : delete the "id" maintenance.
     *
     * @param id the id of the maintenance to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/maintenances/{id}")
    public ResponseEntity<Void> deleteMaintenance(@PathVariable Long id) {
        log.debug("REST request to delete Maintenance : {}", id);
        maintenanceService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
