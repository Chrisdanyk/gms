package cd.gms.repository;

import cd.gms.domain.Operation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Operation entity.
 */
@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    @Query(
        value = "select distinct operation from Operation operation left join fetch operation.mecaniciens left join fetch operation.taches",
        countQuery = "select count(distinct operation) from Operation operation"
    )
    Page<Operation> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct operation from Operation operation left join fetch operation.mecaniciens left join fetch operation.taches")
    List<Operation> findAllWithEagerRelationships();

    @Query(
        "select operation from Operation operation left join fetch operation.mecaniciens left join fetch operation.taches where operation.id =:id"
    )
    Optional<Operation> findOneWithEagerRelationships(@Param("id") Long id);
}
