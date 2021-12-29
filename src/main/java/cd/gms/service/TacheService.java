package cd.gms.service;

import cd.gms.domain.Tache;
import cd.gms.repository.TacheRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Tache}.
 */
@Service
@Transactional
public class TacheService {

    private final Logger log = LoggerFactory.getLogger(TacheService.class);

    private final TacheRepository tacheRepository;

    public TacheService(TacheRepository tacheRepository) {
        this.tacheRepository = tacheRepository;
    }

    /**
     * Save a tache.
     *
     * @param tache the entity to save.
     * @return the persisted entity.
     */
    public Tache save(Tache tache) {
        log.debug("Request to save Tache : {}", tache);
        return tacheRepository.save(tache);
    }

    /**
     * Partially update a tache.
     *
     * @param tache the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Tache> partialUpdate(Tache tache) {
        log.debug("Request to partially update Tache : {}", tache);

        return tacheRepository
            .findById(tache.getId())
            .map(existingTache -> {
                if (tache.getNom() != null) {
                    existingTache.setNom(tache.getNom());
                }
                if (tache.getPrixUnitaire() != null) {
                    existingTache.setPrixUnitaire(tache.getPrixUnitaire());
                }
                if (tache.getDescription() != null) {
                    existingTache.setDescription(tache.getDescription());
                }
                if (tache.getDisponible() != null) {
                    existingTache.setDisponible(tache.getDisponible());
                }
                if (tache.getNuid() != null) {
                    existingTache.setNuid(tache.getNuid());
                }

                return existingTache;
            })
            .map(tacheRepository::save);
    }

    /**
     * Get all the taches.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Tache> findAll(Pageable pageable) {
        log.debug("Request to get all Taches");
        return tacheRepository.findAll(pageable);
    }

    /**
     * Get one tache by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Tache> findOne(Long id) {
        log.debug("Request to get Tache : {}", id);
        return tacheRepository.findById(id);
    }

    /**
     * Delete the tache by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Tache : {}", id);
        tacheRepository.deleteById(id);
    }
}
