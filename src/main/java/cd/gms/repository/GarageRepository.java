package cd.gms.repository;

import cd.gms.domain.Garage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Garage entity.
 */
@Repository
public interface GarageRepository extends JpaRepository<Garage, Long> {
    @Query(
        value = "select distinct garage from Garage garage left join fetch garage.utilisateurs",
        countQuery = "select count(distinct garage) from Garage garage"
    )
    Page<Garage> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct garage from Garage garage left join fetch garage.utilisateurs")
    List<Garage> findAllWithEagerRelationships();

    @Query("select garage from Garage garage left join fetch garage.utilisateurs where garage.id =:id")
    Optional<Garage> findOneWithEagerRelationships(@Param("id") Long id);
}
