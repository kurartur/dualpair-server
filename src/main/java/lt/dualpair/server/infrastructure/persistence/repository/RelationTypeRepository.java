package lt.dualpair.server.infrastructure.persistence.repository;

import lt.dualpair.server.domain.model.socionics.RelationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RelationTypeRepository extends JpaRepository<RelationType, Integer> {

    @Query("select rt from RelationType rt where rt.code = ?1")
    Optional<RelationType> findByCode(RelationType.Code code);

}
