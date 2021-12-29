package cd.gms.web.rest;

import cd.gms.domain.Garage;
import cd.gms.repository.GarageRepository;
import cd.gms.service.GarageService;
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
 * REST controller for managing {@link cd.gms.domain.Garage}.
 */
@RestController
@RequestMapping("/api")
public class GarageResource {

    private final Logger log = LoggerFactory.getLogger(GarageResource.class);

    private static final String ENTITY_NAME = "garage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GarageService garageService;

    private final GarageRepository garageRepository;

    public GarageResource(GarageService garageService, GarageRepository garageRepository) {
        this.garageService = garageService;
        this.garageRepository = garageRepository;
    }

    /**
     * {@code POST  /garages} : Create a new garage.
     *
     * @param garage the garage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new garage, or with status {@code 400 (Bad Request)} if the garage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/garages")
    public ResponseEntity<Garage> createGarage(@Valid @RequestBody Garage garage) throws URISyntaxException {
        log.debug("REST request to save Garage : {}", garage);
        if (garage.getId() != null) {
            throw new BadRequestAlertException("A new garage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Garage result = garageService.save(garage);
        return ResponseEntity
            .created(new URI("/api/garages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /garages/:id} : Updates an existing garage.
     *
     * @param id the id of the garage to save.
     * @param garage the garage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated garage,
     * or with status {@code 400 (Bad Request)} if the garage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the garage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/garages/{id}")
    public ResponseEntity<Garage> updateGarage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Garage garage
    ) throws URISyntaxException {
        log.debug("REST request to update Garage : {}, {}", id, garage);
        if (garage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, garage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!garageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Garage result = garageService.save(garage);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, garage.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /garages/:id} : Partial updates given fields of an existing garage, field will ignore if it is null
     *
     * @param id the id of the garage to save.
     * @param garage the garage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated garage,
     * or with status {@code 400 (Bad Request)} if the garage is not valid,
     * or with status {@code 404 (Not Found)} if the garage is not found,
     * or with status {@code 500 (Internal Server Error)} if the garage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/garages/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Garage> partialUpdateGarage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Garage garage
    ) throws URISyntaxException {
        log.debug("REST request to partial update Garage partially : {}, {}", id, garage);
        if (garage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, garage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!garageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Garage> result = garageService.partialUpdate(garage);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, garage.getId().toString())
        );
    }

    /**
     * {@code GET  /garages} : get all the garages.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of garages in body.
     */
    @GetMapping("/garages")
    public ResponseEntity<List<Garage>> getAllGarages(
        Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Garages");
        Page<Garage> page;
        if (eagerload) {
            page = garageService.findAllWithEagerRelationships(pageable);
        } else {
            page = garageService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /garages/:id} : get the "id" garage.
     *
     * @param id the id of the garage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the garage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/garages/{id}")
    public ResponseEntity<Garage> getGarage(@PathVariable Long id) {
        log.debug("REST request to get Garage : {}", id);
        Optional<Garage> garage = garageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(garage);
    }

    /**
     * {@code DELETE  /garages/:id} : delete the "id" garage.
     *
     * @param id the id of the garage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/garages/{id}")
    public ResponseEntity<Void> deleteGarage(@PathVariable Long id) {
        log.debug("REST request to delete Garage : {}", id);
        garageService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
