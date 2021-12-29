package cd.gms.service;

import cd.gms.domain.Maintenance;
import cd.gms.repository.MaintenanceRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Maintenance}.
 */
@Service
@Transactional
public class MaintenanceService {

    private final Logger log = LoggerFactory.getLogger(MaintenanceService.class);

    private final MaintenanceRepository maintenanceRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    /**
     * Save a maintenance.
     *
     * @param maintenance the entity to save.
     * @return the persisted entity.
     */
    public Maintenance save(Maintenance maintenance) {
        log.debug("Request to save Maintenance : {}", maintenance);
        return maintenanceRepository.save(maintenance);
    }

    /**
     * Partially update a maintenance.
     *
     * @param maintenance the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Maintenance> partialUpdate(Maintenance maintenance) {
        log.debug("Request to partially update Maintenance : {}", maintenance);

        return maintenanceRepository
            .findById(maintenance.getId())
            .map(existingMaintenance -> {
                if (maintenance.getDateDebut() != null) {
                    existingMaintenance.setDateDebut(maintenance.getDateDebut());
                }
                if (maintenance.getDateFin() != null) {
                    existingMaintenance.setDateFin(maintenance.getDateFin());
                }
                if (maintenance.getRapportGlobal() != null) {
                    existingMaintenance.setRapportGlobal(maintenance.getRapportGlobal());
                }
                if (maintenance.getPrixTotal() != null) {
                    existingMaintenance.setPrixTotal(maintenance.getPrixTotal());
                }
                if (maintenance.getDiscountTotal() != null) {
                    existingMaintenance.setDiscountTotal(maintenance.getDiscountTotal());
                }
                if (maintenance.getNuid() != null) {
                    existingMaintenance.setNuid(maintenance.getNuid());
                }

                return existingMaintenance;
            })
            .map(maintenanceRepository::save);
    }

    /**
     * Get all the maintenances.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Maintenance> findAll(Pageable pageable) {
        log.debug("Request to get all Maintenances");
        return maintenanceRepository.findAll(pageable);
    }

    /**
     * Get all the maintenances with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Maintenance> findAllWithEagerRelationships(Pageable pageable) {
        return maintenanceRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one maintenance by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Maintenance> findOne(Long id) {
        log.debug("Request to get Maintenance : {}", id);
        return maintenanceRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the maintenance by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Maintenance : {}", id);
        maintenanceRepository.deleteById(id);
    }
}
