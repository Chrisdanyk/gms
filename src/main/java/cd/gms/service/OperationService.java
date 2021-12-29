package cd.gms.service;

import cd.gms.domain.Operation;
import cd.gms.repository.OperationRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Operation}.
 */
@Service
@Transactional
public class OperationService {

    private final Logger log = LoggerFactory.getLogger(OperationService.class);

    private final OperationRepository operationRepository;

    public OperationService(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    /**
     * Save a operation.
     *
     * @param operation the entity to save.
     * @return the persisted entity.
     */
    public Operation save(Operation operation) {
        log.debug("Request to save Operation : {}", operation);
        return operationRepository.save(operation);
    }

    /**
     * Partially update a operation.
     *
     * @param operation the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Operation> partialUpdate(Operation operation) {
        log.debug("Request to partially update Operation : {}", operation);

        return operationRepository
            .findById(operation.getId())
            .map(existingOperation -> {
                if (operation.getDate() != null) {
                    existingOperation.setDate(operation.getDate());
                }
                if (operation.getPrix() != null) {
                    existingOperation.setPrix(operation.getPrix());
                }
                if (operation.getDiscount() != null) {
                    existingOperation.setDiscount(operation.getDiscount());
                }
                if (operation.getNuid() != null) {
                    existingOperation.setNuid(operation.getNuid());
                }

                return existingOperation;
            })
            .map(operationRepository::save);
    }

    /**
     * Get all the operations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Operation> findAll(Pageable pageable) {
        log.debug("Request to get all Operations");
        return operationRepository.findAll(pageable);
    }

    /**
     * Get all the operations with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Operation> findAllWithEagerRelationships(Pageable pageable) {
        return operationRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one operation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Operation> findOne(Long id) {
        log.debug("Request to get Operation : {}", id);
        return operationRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the operation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Operation : {}", id);
        operationRepository.deleteById(id);
    }
}
