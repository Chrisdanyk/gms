package cd.gms.repository;

import cd.gms.domain.Engin;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Engin entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EnginRepository extends JpaRepository<Engin, Long> {
    @Query("select engin from Engin engin where engin.proprietaire.login = ?#{principal.username}")
    List<Engin> findByProprietaireIsCurrentUser();
}
