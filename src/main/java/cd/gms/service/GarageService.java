package cd.gms.service;

import cd.gms.domain.Garage;
import cd.gms.repository.GarageRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Garage}.
 */
@Service
@Transactional
public class GarageService {

    private final Logger log = LoggerFactory.getLogger(GarageService.class);

    private final GarageRepository garageRepository;

    public GarageService(GarageRepository garageRepository) {
        this.garageRepository = garageRepository;
    }

    /**
     * Save a garage.
     *
     * @param garage the entity to save.
     * @return the persisted entity.
     */
    public Garage save(Garage garage) {
        log.debug("Request to save Garage : {}", garage);
        return garageRepository.save(garage);
    }

    /**
     * Partially update a garage.
     *
     * @param garage the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Garage> partialUpdate(Garage garage) {
        log.debug("Request to partially update Garage : {}", garage);

        return garageRepository
            .findById(garage.getId())
            .map(existingGarage -> {
                if (garage.getNom() != null) {
                    existingGarage.setNom(garage.getNom());
                }
                if (garage.getAddresse() != null) {
                    existingGarage.setAddresse(garage.getAddresse());
                }
                if (garage.getEmail() != null) {
                    existingGarage.setEmail(garage.getEmail());
                }
                if (garage.getTelephone() != null) {
                    existingGarage.setTelephone(garage.getTelephone());
                }
                if (garage.getRccm() != null) {
                    existingGarage.setRccm(garage.getRccm());
                }
                if (garage.getUrl() != null) {
                    existingGarage.setUrl(garage.getUrl());
                }
                if (garage.getNuid() != null) {
                    existingGarage.setNuid(garage.getNuid());
                }

                return existingGarage;
            })
            .map(garageRepository::save);
    }

    /**
     * Get all the garages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Garage> findAll(Pageable pageable) {
        log.debug("Request to get all Garages");
        return garageRepository.findAll(pageable);
    }

    /**
     * Get all the garages with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Garage> findAllWithEagerRelationships(Pageable pageable) {
        return garageRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one garage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Garage> findOne(Long id) {
        log.debug("Request to get Garage : {}", id);
        return garageRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the garage by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Garage : {}", id);
        garageRepository.deleteById(id);
    }
}
