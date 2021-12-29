package cd.gms.web.rest;

import cd.gms.domain.Engin;
import cd.gms.repository.EnginRepository;
import cd.gms.service.EnginService;
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
 * REST controller for managing {@link cd.gms.domain.Engin}.
 */
@RestController
@RequestMapping("/api")
public class EnginResource {

    private final Logger log = LoggerFactory.getLogger(EnginResource.class);

    private static final String ENTITY_NAME = "engin";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnginService enginService;

    private final EnginRepository enginRepository;

    public EnginResource(EnginService enginService, EnginRepository enginRepository) {
        this.enginService = enginService;
        this.enginRepository = enginRepository;
    }

    /**
     * {@code POST  /engins} : Create a new engin.
     *
     * @param engin the engin to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new engin, or with status {@code 400 (Bad Request)} if the engin has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/engins")
    public ResponseEntity<Engin> createEngin(@Valid @RequestBody Engin engin) throws URISyntaxException {
        log.debug("REST request to save Engin : {}", engin);
        if (engin.getId() != null) {
            throw new BadRequestAlertException("A new engin cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Engin result = enginService.save(engin);
        return ResponseEntity
            .created(new URI("/api/engins/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /engins/:id} : Updates an existing engin.
     *
     * @param id the id of the engin to save.
     * @param engin the engin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated engin,
     * or with status {@code 400 (Bad Request)} if the engin is not valid,
     * or with status {@code 500 (Internal Server Error)} if the engin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/engins/{id}")
    public ResponseEntity<Engin> updateEngin(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Engin engin)
        throws URISyntaxException {
        log.debug("REST request to update Engin : {}, {}", id, engin);
        if (engin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, engin.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!enginRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Engin result = enginService.save(engin);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, engin.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /engins/:id} : Partial updates given fields of an existing engin, field will ignore if it is null
     *
     * @param id the id of the engin to save.
     * @param engin the engin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated engin,
     * or with status {@code 400 (Bad Request)} if the engin is not valid,
     * or with status {@code 404 (Not Found)} if the engin is not found,
     * or with status {@code 500 (Internal Server Error)} if the engin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/engins/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Engin> partialUpdateEngin(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Engin engin
    ) throws URISyntaxException {
        log.debug("REST request to partial update Engin partially : {}, {}", id, engin);
        if (engin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, engin.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!enginRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Engin> result = enginService.partialUpdate(engin);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, engin.getId().toString())
        );
    }

    /**
     * {@code GET  /engins} : get all the engins.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of engins in body.
     */
    @GetMapping("/engins")
    public ResponseEntity<List<Engin>> getAllEngins(Pageable pageable) {
        log.debug("REST request to get a page of Engins");
        Page<Engin> page = enginService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /engins/:id} : get the "id" engin.
     *
     * @param id the id of the engin to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the engin, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/engins/{id}")
    public ResponseEntity<Engin> getEngin(@PathVariable Long id) {
        log.debug("REST request to get Engin : {}", id);
        Optional<Engin> engin = enginService.findOne(id);
        return ResponseUtil.wrapOrNotFound(engin);
    }

    /**
     * {@code DELETE  /engins/:id} : delete the "id" engin.
     *
     * @param id the id of the engin to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/engins/{id}")
    public ResponseEntity<Void> deleteEngin(@PathVariable Long id) {
        log.debug("REST request to delete Engin : {}", id);
        enginService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
