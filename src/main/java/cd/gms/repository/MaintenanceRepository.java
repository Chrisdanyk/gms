package cd.gms.repository;

import cd.gms.domain.Maintenance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Maintenance entity.
 */
@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    @Query(
        value = "select distinct maintenance from Maintenance maintenance left join fetch maintenance.operations",
        countQuery = "select count(distinct maintenance) from Maintenance maintenance"
    )
    Page<Maintenance> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct maintenance from Maintenance maintenance left join fetch maintenance.operations")
    List<Maintenance> findAllWithEagerRelationships();

    @Query("select maintenance from Maintenance maintenance left join fetch maintenance.operations where maintenance.id =:id")
    Optional<Maintenance> findOneWithEagerRelationships(@Param("id") Long id);
}
